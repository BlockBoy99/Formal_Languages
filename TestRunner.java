import java.io.*;

public class TestRunner {
    static int pass = 0;
    static int fail = 0;

    public static void main(String[] args) throws Exception {
        // format: testName, input, expectedStdout, expectedStderr
        runTest("basic halt",
            "A: if x<1 (x,y) halt else (x-1,y) and A;\nrun A (3,0)",
            "Success\nSimple\nhalt 0 0", "");

        runTest("spec example 1",
            "Countdown: if x<10 (x,y) halt else (x-10,y) and Countdown;\nrun Countdown (2025, 0)",
            "Success\nSimple\nhalt 5 0", "");

        runTest("spec example 3",
            "m3: if a < 1\n(a, b)\nend\nelse\n(a - 1, b + 3) and m3 ;\nrun m3 (10, 1)",
            "Success\nSimple\nend 0 31", "");

        runTest("spec example 4 loop",
            "L: if x<3 (x,y) becomes (y,x) and L\nelse (x-3, y+3) and R;\nrun L (0, 2)",
            "Success\nSimple\nLoop", "");

        runTest("short form both branches omitted",
            "A: if x<5 (x,y) halt else halt;\nrun A (3,0)",
            "Success\nSimple\nhalt 3 0", "");

        runTest("short form true branch omitted",
            "A: if x<5 (x,y) halt else (x-1,y) and A;\nrun A (3,0)",
            "Success\nSimple\nhalt 3 0", "");

        runTest("short form false branch omitted",
            "A: if x<5 (x,y) becomes (x+1,y) and A else halt;\nrun A (0,0)",
            "Success\nSimple\nhalt 5 0", "");

        runTest("run starts at zero",
            "A: if x<1 (x,y) halt else (x-1,y) and A;\nrun A (0,0)",
            "Success\nSimple\nhalt 0 0", "");

        runTest("forward reference",
            "A: if x<5 (x,y) becomes (x+1,y) and B else halt;\nB: if x<10 (x,y) becomes (x+1,y) and A else halt;\nrun A (0,0)",
            "Success\nSimple\nhalt 6 0", "");

        runTest("condition uses ID2",
            "A: if y<5 (x,y) halt else (x,y-1) and A;\nrun A (0,7)",
            "Success\nSimple\nhalt 0 4", "");

        runTest("case sensitive identifiers",
            "STEP: if x<5 (x,y) halt else (x-1,y) and STEP;\nstep: if x<5 (x,y) halt else (x-1,y) and step;\nrun STEP (3,0)",
            "Success\nSimple\nhalt 3 0", "");

        runTest("self reference loop",
            "A: if x<5 (x,y) becomes (x+1,y) and A else (x-1,y) and A;\nrun A (3,0)",
            "Success\nSimple\nLoop", "");

        runTest("two steps",
            "A: if x<5 (x,y) becomes (x+1,y) and A else B;\nB: if y<5 (x,y) becomes (x,y+1) and B else halt;\nrun A (0,0)",
            "Success\nSimple\nhalt 5 5", "");

        // non-simple
        runTest("spec example 2 non-simple",
            "Q: if n<2 (n,p) RET1 else (n,n) and parity;\nparity: if p<2 (n,p) pardec else (n, p-2) and parity;\npardec: if r<1 (n,r) becomes (n,0) and even else (n,0) and odd;\nodd: if x<100000 (x,y) becomes (3*x+1, y) and Q else OVERFLOW;\neven: if a<2 (a,b) becomes (b,a) and Q else (a-2,b+1) and even;\nrun Q (12,0)",
            "Success\nNon-simple\nodd", "");

        runTest("multiplication non-simple",
            "A: if x<5 (x,y) becomes (x*2,y) and A else halt;\nrun A (1,0)",
            "Success\nNon-simple\nA", "");

        runTest("subtraction in true branch non-simple",
            "A: if x<5 (x,y) becomes (x-1,y) and A else halt;\nrun A (3,0)",
            "Success\nNon-simple\nA", "");

        runTest("first non-simple reported",
            "A: if x<5 (x,y) becomes (x*2,y) and A else halt;\nB: if x<5 (x,y) becomes (x*3,y) and B else halt;\nrun A (1,0)",
            "Success\nNon-simple\nA", "");

        runTest("s3 violation non-simple",
            "A: if x<5 (x,y) halt else (x-10,y) and halt;\nrun A (5,0)",
            "Success\nNon-simple\nA", "");

        // invalid
        runTest("no steps before run",
            "run A (1,1)",
            "Failure", "1\nProgram must contain at least one step definition before the run instruction.");

        runTest("semicolon after run",
            "A: if x<5 (x,y) halt else halt;\nrun A (1,1);",
            "Failure", "2\nUnexpected semicolon after the run instruction.");

        runTest("duplicate step name",
            "A: if x<5 (x,y) halt else halt;\nA: if x<5 (x,y) halt else halt;\nrun A (1,1)",
            "Failure", "2\nDuplicate step name: A");

        runTest("run step missing",
            "A: if x<5 (x,y) halt else halt;\nrun B (1,1)",
            "Failure", "2\nStep 'B' mentioned in run instruction does not exist.");

        runTest("param used as step name",
            "A: if x<5 (x,y) halt else halt;\nx: if a<5 (a,b) halt else halt;\nrun A (1,1)",
            "Failure", "2\nIdentifier 'x' is already used as a parameter and cannot be a step name.");

        runTest("step name used as param",
            "A: if x<5 (x,y) halt else halt;\nB: if A<5 (A,b) halt else halt;\nrun A (1,1)",
            "Failure", "2\nIdentifier 'A' is already used as a step name/reference and cannot be a parameter.");

        runTest("id1 equals id2",
            "A: if x<5 (x,x) halt else halt;\nrun A (1,1)",
            "Failure", "1\nParameter names must be different in step: A");

        runTest("condition not using params",
            "A: if z<5 (x,y) halt else halt;\nrun A (1,1)",
            "Failure", "1\nCondition uses parameter 'z' but must use either 'x' or 'y'.");

        runTest("condition with parens",
            "A: if (x<5) (x,y) halt else halt;\nrun A (1,1)",
            "Failure", "1\nSyntax error: expected an identifier after \"if\"; got \"(\".");

        runTest("double semicolon",
            "A: if x<5 (x,y) halt else halt;;\nrun A (1,1)",
            "Failure", "1\nUnexpected semicolon.");

        runTest("unknown identifier in expression",
            "A: if x<5 (x,y) becomes (z+1,y) and halt else halt;\nrun A (1,1)",
            "Failure", "1\nIdentifier 'z' is not a parameter in this step.");
        //hallo
        runTest("fast forward valid jump (bug fix 1.1)",
            "S1: if x<5 (x,y) becomes (10,y) and S1 else (x,y+1) and halt;\nrun S1 (0,0)",
            "Success\nSimple\nhalt 10 1", "");

        runTest("true constant loop",
            "S1: if x<5 (x,y) becomes (2,y) and S1 else (x,y+1) and halt;\nrun S1 (0,0)",
            "Success\nSimple\nLoop", "");

        runTest("keyword as identifier error (bug fix 2)",
            "s1: if x<5 (x,y) becomes (1,1) and run else halt;\nrun s1 (0,0)",
            "Failure", "1\nKeyword 'run' cannot be used as an identifier.");

        runTest("arithmetic in condition error (bug fix 2)",
            "s1: if x+1<5 (x,y) halt else halt;\nrun s1 (0,0)",
            "Failure", "1\nCondition is not of the form ID<NUM; found arithmetic operation: '+'.");

        runTest("unexpected token at EOF (bug fix 3 t.image)",
            "s1: if x<5 (x,y) halt else halt;\nrun s1 (0,0) @",
            "Failure", "2\nUnexpected character: '@'");

        // Tests for S3 strict boundary evaluating to exactly 0 (Should be Simple)
        runTest("s3 exact boundary simple",
            "A: if x<5 (x,y) becomes (x,y) and A else (x-5,y) and die;\nrun A (6,0)",
            "Success\nSimple\ndie 1 0", "");

        // Tests for S3 strict boundary evaluating to -1 at the limit (Should be Non-Simple)
        runTest("s3 exact boundary non-simple",
            "A: if x<5 (x,y) becomes (x,y) and A else (x-6,y) and halt;\nrun A (6,0)",
            "Success\nNon-simple\nA", "");

        // Tests that Hare and Tortoise correctly captures an alternating multi-step loop
        runTest("alternating state loop",
            "A: if x<10 (x,y) becomes (x-1,y) and B else halt;\nB: if x<10 (x,y) becomes (x+1,y) and A else halt;\nrun A (0,0)",
            "Success\nNon-simple\nA", "");

        // Tests mathematical acceleration for massive loops (The Extra Challenge)
        runTest("massive fast forward",
            "A: if x<1000000 (x,y) becomes (x+1,y) and A else halt;\nrun A (0,0)",
            "Success\nSimple\nhalt 1000000 0", "");

        runTest("nested arithmetic parens",
            "A: if x<1000 (x,y) becomes (x+5,y) and A else (x+(2-3), y) and A;\nrun A (1,0)",
            "erorrrr", "");

        // Tests flattening variables and constants accurately
        runTest("complex arithmetic evaluation",
            "A: if x<1 (x,y) becomes (x+y+y+19, y) and A else halt;\nrun A (0,5)",
            "Success\nSimple\nhalt 29 5", "");

        runTest("missing comma in params error",
            "A: if x<5 (x y) halt else halt;\nrun A (0,0)",
            "Failure", "1");

        // Tests Rule p2 Violation (Cannot subtract parameters, only numerals)
        runTest("subtracting variable p2 violation",
            "A: if x<5 (x,y) becomes (x,y) and A else (x-y, y) and halt;\nrun A (0,0)",
            "Success\nNon-simple\nA", "");

        // According to Section 1.5, only spaces and line breaks are allowed as whitespace. Tabs are forbidden.
        runTest("tab character is invalid whitespace",
            "A:\tif x<5 (x,y) halt else halt;\nrun A (0,0)",
            "Failure", "1");
            
        System.out.println("\nResults: " + pass + " passed, " + fail + 
            " failed out of " + (pass + fail) + " tests");
    }

    static void runTest(String name, String input, String expectedOut, String expectedErr) 
        throws Exception {
        
        ProcessBuilder pb = new ProcessBuilder("java", "TPTP");
        pb.redirectErrorStream(false);
        Process process = pb.start();

        // write input
        OutputStream stdin = process.getOutputStream();
        stdin.write(input.getBytes());
        stdin.close();

        // read stdout
        String actualOut = new String(process.getInputStream().readAllBytes()).trim()
            .replace("\r\n", "\n");
        // read stderr
        String actualErr = new String(process.getErrorStream().readAllBytes()).trim()
            .replace("\r\n", "\n");

        // strip java options line if present
        if (actualErr.startsWith("Picked up")) {
            int nl = actualErr.indexOf('\n');
            actualErr = nl >= 0 ? actualErr.substring(nl + 1).trim() : "";
        }

        boolean outMatch = actualOut.equals(expectedOut);
        // for invalid programs only check stderr starts with line number
        // since error messages are evaluated manually
        boolean errMatch;
        if (expectedErr.isEmpty()) {
            errMatch = actualErr.isEmpty();
        } else {
            // check line number matches at minimum
            String expectedLine = expectedErr.split("\n")[0];
            errMatch = actualErr.startsWith(expectedLine);
        }

        if (outMatch && errMatch) {
            System.out.println("PASS: " + name);
            System.out.println("  Got stdout:      " + actualOut.replace("\n", "\\n"));

            pass++;
        } else {
            System.out.println("FAIL: " + name);
            if (!outMatch) {
                System.out.println("  Expected stdout: " + expectedOut.replace("\n", "\\n"));
                System.out.println("  Got stdout:      " + actualOut.replace("\n", "\\n"));
            }
            if (!errMatch) {
                System.out.println("  Expected stderr: " + expectedErr.replace("\n", "\\n"));
                System.out.println("  Got stderr:      " + actualErr.replace("\n", "\\n"));
            }
            fail++;
        }
    }
}