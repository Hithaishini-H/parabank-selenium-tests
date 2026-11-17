package com.parabank.whitebox;

import java.math.BigDecimal;

/**
 * White-box unit under test: the validation rules a fund-transfer must satisfy
 * (mirrors what ParaBank's Transfer Funds screen enforces / should enforce).
 *
 * Predicate nodes (P) are numbered in the comments for the Control Flow Graph:
 *   P1..P12  ->  V(G) = P + 1 = 13  independent paths.
 * See docs/TransferValidator_CFG.png and docs/TEST_DESIGN.md.
 */
public final class TransferValidator {

    public static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");
    public static final BigDecimal MAX_AMOUNT = new BigDecimal("100000.00");

    public enum Result {
        OK,
        EMPTY_AMOUNT,
        NOT_NUMERIC,
        NON_POSITIVE,
        TOO_MANY_DECIMALS,
        EXCEEDS_LIMIT,
        MISSING_ACCOUNT,
        SAME_ACCOUNT,
        INSUFFICIENT_FUNDS
    }

    private TransferValidator() {}

    public static Result validate(String amountText, String fromId, String toId, BigDecimal balance) {
        if (amountText == null || amountText.trim().isEmpty()) {            // P1, P2
            return Result.EMPTY_AMOUNT;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim());
        } catch (NumberFormatException ex) {                                // P3
            return Result.NOT_NUMERIC;
        }

        if (amount.signum() <= 0) {                                         // P4
            return Result.NON_POSITIVE;
        }
        if (amount.scale() > 2) {                                           // P5
            return Result.TOO_MANY_DECIMALS;
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {                             // P6
            return Result.EXCEEDS_LIMIT;
        }

        if (fromId == null || fromId.trim().isEmpty()                       // P7, P8
                || toId == null || toId.trim().isEmpty()) {                 // P9, P10
            return Result.MISSING_ACCOUNT;
        }
        if (fromId.equals(toId)) {                                          // P11
            return Result.SAME_ACCOUNT;
        }
        if (amount.compareTo(balance) > 0) {                                // P12
            return Result.INSUFFICIENT_FUNDS;
        }
        return Result.OK;
    }
}
