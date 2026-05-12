echo ----------------------------------
java TPTP <tests/in1.txt # PASS
echo ----------------------------------
java TPTP <tests/in2.txt # PASS
echo ----------------------------------
java TPTP <tests/in3.txt # PASS
echo ----------------------------------
java TPTP <tests/in4.txt # PASS
echo ----------------------------------
java TPTP <tests/in5.txt # FAIL due to condition in brackets
echo ----------------------------------
java TPTP <tests/in6.txt # FAIL due to semicolon after run
echo ----------------------------------
java TPTP <tests/in7.txt # FAIL for many reasons
echo badNumberPair----------------------------------
java TPTP <tests/badNumberPair.txt # FAIL
echo badRunStep----------------------------------
java TPTP <tests/badRunStep.txt # FAIL
echo missingRunKeyword----------------------------------
java TPTP <tests/missingRunKeyword.txt # FAIL
echo nonExistentIdentifier----------------------------------
java TPTP <tests/nonExistentIdentifier.txt # FAIL
echo identifierInUse----------------------------------
java TPTP <tests/identifierInUse.txt # FAIL
echo identifierInUse2----------------------------------
java TPTP <tests/identifierInUse2.txt # FAIL
echo badStepName----------------------------------
java TPTP <tests/badStepName.txt # FAIL
echo badStepName2----------------------------------
java TPTP <tests/badStepName2.txt # FAIL - reports to be a run line error, since it instruction fails, so it assumes the + ends there
echo badStepName3----------------------------------
java TPTP <tests/badStepName3.txt # FAIL
echo ----------------------------------
java TPTP <tests/infiniteUp.txt # FAIL for many reasons
echo ----------------------------------
java TPTP <tests/infiniteUp2.txt # FAIL for many reasons
