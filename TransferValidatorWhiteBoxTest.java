package com.parabank.tests;

import com.parabank.whitebox.CyclomaticComplexityCalculator;
import com.parabank.whitebox.TransferValidator;
import com.parabank.whitebox.TransferValidator.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure-Java tests (no browser needed, run in milliseconds):
 *   1. Cyclomatic complexity of the unit            -> V(G) = 13
 *   2. Basis-path coverage (13 independent paths)   -> every path executed once
 *   3. ECP + BVA on the amount rule [0.01 .. 100000.00]
 */
class TransferValidatorWhiteBoxTest {

    private static final BigDecimal RICH = new BigDecimal("1000000.00");

    // ---------------------------------------------------------------- 1. complexity
    @Test
    @DisplayName("Cyclomatic complexity V(G) = P + 1 = 13")
    void cyclomaticComplexityIs13() throws IOException {
        Path src = Path.of("src/main/java/com/parabank/whitebox/TransferValidator.java");
        assertEquals(12, CyclomaticComplexityCalculator.predicates(java.nio.file.Files.readString(src)));
        assertEquals(13, CyclomaticComplexityCalculator.complexityOfFile(src));
    }

    // ---------------------------------------------------------------- 2. basis paths
    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', nullValues = "NULL", value = {
            //  path | amount     | from | to  | balance    | expected
            "Path 1  - amount null            | NULL       | 101  | 102  | 1000000.00 | EMPTY_AMOUNT",
            "Path 2  - amount blank           | '   '      | 101  | 102  | 1000000.00 | EMPTY_AMOUNT",
            "Path 3  - not a number            | abc        | 101  | 102  | 1000000.00 | NOT_NUMERIC",
            "Path 4  - zero (non-positive)     | 0          | 101  | 102  | 1000000.00 | NON_POSITIVE",
            "Path 5  - 3 decimal places        | 10.555     | 101  | 102  | 1000000.00 | TOO_MANY_DECIMALS",
            "Path 6  - above max               | 100000.01  | 101  | 102  | 1000000.00 | EXCEEDS_LIMIT",
            "Path 7  - from null               | 50.00      | NULL | 102  | 1000000.00 | MISSING_ACCOUNT",
            "Path 8  - from blank              | 50.00      | '  ' | 102  | 1000000.00 | MISSING_ACCOUNT",
            "Path 9  - to null                 | 50.00      | 101  | NULL | 1000000.00 | MISSING_ACCOUNT",
            "Path 10 - to blank                | 50.00      | 101  | '  ' | 1000000.00 | MISSING_ACCOUNT",
            "Path 11 - same account            | 50.00      | 101  | 101  | 1000000.00 | SAME_ACCOUNT",
            "Path 12 - amount > balance        | 100.01     | 101  | 102  | 100.00     | INSUFFICIENT_FUNDS",
            "Path 13 - all checks pass         | 50.00      | 101  | 102  | 1000.00    | OK"
    })
    @DisplayName("White-box: 13 basis paths")
    void basisPaths(String path, String amount, String from, String to, String balance, Result expected) {
        assertEquals(expected, TransferValidator.validate(amount, from, to, new BigDecimal(balance)), path);
    }

    // ---------------------------------------------------------------- 3a. ECP
    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', nullValues = "NULL", value = {
            "V1 valid: typical amount          | 250.75     | OK",
            "V2 valid: integer amount          | 10         | OK",
            "I1 invalid: empty                 | ''         | EMPTY_AMOUNT",
            "I2 invalid: letters               | abc        | NOT_NUMERIC",
            "I3 invalid: symbols               | $$         | NOT_NUMERIC",
            "I4 invalid: negative              | -5.00      | NON_POSITIVE",
            "I5 invalid: zero                  | 0.00       | NON_POSITIVE",
            "I6 invalid: > 2 decimals          | 1.999      | TOO_MANY_DECIMALS",
            "I7 invalid: above upper limit     | 250000.00  | EXCEEDS_LIMIT"
    })
    @DisplayName("ECP on amount (valid vs invalid classes)")
    void equivalenceClasses(String label, String amount, Result expected) {
        assertEquals(expected, TransferValidator.validate(amount, "101", "102", RICH), label);
    }

    // ---------------------------------------------------------------- 3b. BVA
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(delimiter = '|', value = {
            "Min-1   | 0.00       | NON_POSITIVE",
            "Min     | 0.01       | OK",
            "Min+1   | 0.02       | OK",
            "Nominal | 50000.00   | OK",
            "Max-1   | 99999.99   | OK",
            "Max     | 100000.00  | OK",
            "Max+1   | 100000.01  | EXCEEDS_LIMIT"
    })
    @DisplayName("BVA on amount range [0.01, 100000.00]")
    void boundaryValues(String label, String amount, Result expected) {
        assertEquals(expected, TransferValidator.validate(amount, "101", "102", RICH), label);
    }
}
