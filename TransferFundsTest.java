package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.OpenAccountPage;
import com.parabank.pages.TransferFundsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end transfer tests: register -> open 2nd account -> transfer.
 * Oracle: amount numeric, >= 0.01 and <= available balance of the source account.
 * A failing Max+1 / negative row means ParaBank deviates from this spec (report it as a finding).
 */
class TransferFundsTest extends BaseTest {

    private String fromAccount;
    private String toAccount;

    @BeforeEach
    void prepareTwoAccounts() {
        registerNewUser();
        AccountsOverviewPage overview = overview();
        List<String> existing = overview.getAccountIds();
        assertFalse(existing.isEmpty(),
                "Newly registered customer has no account on Accounts Overview (cannot fund a 2nd account)");

        fromAccount = existing.get(0);
        toAccount = overview.goToOpenNewAccount().openAccount(OpenAccountPage.Type.SAVINGS);
        assertNotEquals(fromAccount, toAccount, "Second account must have a different number");
    }

    @Test
    @DisplayName("Account dropdowns are populated and list both accounts")
    void verifyAccountDropdownAvailability() {
        TransferFundsPage page = overview().goToTransferFunds();

        assertTrue(page.areDropdownsPopulated(), "From/To dropdowns should be populated");
        assertTrue(page.getFromAccounts().contains(fromAccount), "From list missing " + fromAccount);
        assertTrue(page.getFromAccounts().contains(toAccount), "From list missing " + toAccount);
        assertTrue(page.getToAccounts().contains(fromAccount), "To list missing " + fromAccount);
        assertTrue(page.getToAccounts().contains(toAccount), "To list missing " + toAccount);
    }

    @Test
    @DisplayName("Happy path: valid transfer completes and source balance drops")
    void verifyValidTransfers() {
        BigDecimal fromBefore = overview().getBalance(fromAccount);

        TransferFundsPage page = overview().goToTransferFunds().transfer("25.00", fromAccount, toAccount);
        assertTrue(page.isTransferSuccessful(), "Transfer Complete! expected");

        BigDecimal fromAfter = page.goToAccountsOverview().getBalance(fromAccount);
        assertEquals(0, fromBefore.subtract(new BigDecimal("25.00")).compareTo(fromAfter),
                "Source balance should drop by exactly 25.00 (before=" + fromBefore + ", after=" + fromAfter + ")");
    }

    /** BVA: MIN = 0.01 (absolute); MAX = live balance of source account (delta added). */
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource({
            "Min-1  (0.00),            ABS,  0.00,  false",
            "Min    (0.01),            ABS,  0.01,  true",
            "Min+1  (0.02),            ABS,  0.02,  true",
            "Nominal (50.00),          ABS,  50.00, true",
            "Max-1  (balance-0.01),    BAL, -0.01,  true",
            "Max    (balance),         BAL,  0.00,  true",
            "Max+1  (balance+0.01),    BAL,  0.01,  false"
    })
    @DisplayName("BVA: transfer amount boundaries")
    void amountBoundaries(String label, String base, String value, boolean expectSuccess) {
        BigDecimal amount = new BigDecimal(value);
        if ("BAL".equals(base)) {
            amount = overview().getBalance(fromAccount).add(amount);
        }

        TransferFundsPage page = overview().goToTransferFunds()
                .transfer(amount.setScale(2).toPlainString(), fromAccount, toAccount);

        if (expectSuccess) {
            assertTrue(page.isTransferSuccessful(), label + " should be accepted");
        } else {
            assertFalse(page.isTransferSuccessful(), label + " should be rejected");
        }
    }

    @ParameterizedTest(name = "amount \"{0}\" is rejected")
    @ValueSource(strings = {"", "abc", "$$$", "-5.00"})
    @DisplayName("ECP: invalid amount classes are rejected")
    void invalidAmountClasses(String amount) {
        TransferFundsPage page = overview().goToTransferFunds().transfer(amount, fromAccount, toAccount);
        assertFalse(page.isTransferSuccessful(), "'" + amount + "' must not complete a transfer");
        assertTrue(page.isErrorShown(), "An error should be visible for '" + amount + "'");
    }
}
