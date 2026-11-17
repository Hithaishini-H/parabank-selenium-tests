package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.BillPayPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Bill Pay: one valid class and the main invalid classes (empty payee, account mismatch, bad amount). */
class BillPayTest extends BaseTest {

    private String fromAccount;

    @BeforeEach
    void register() {
        registerNewUser();
        AccountsOverviewPage overview = overview();
        assumeTrue(!overview.getAccountIds().isEmpty(), "Customer has no account to pay from");
        fromAccount = overview.getAccountIds().get(0);
    }

    @Test
    @DisplayName("ECP valid class: complete payee details + positive amount")
    void validPayment() {
        BillPayPage page = overview().goToBillPay()
                .fillPayee("Electricity Board", "1 Power St", "Chennai", "TN", "600001",
                        "9123456780", "12345", "12345")
                .pay("30.00", fromAccount);

        assertTrue(page.isPaymentSuccessful(), "Bill Payment Complete expected");
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource(delimiter = '|', value = {
            "empty payee name      | ''      | 12345 | 12345 | 30.00",
            "account mismatch      | Vendor  | 12345 | 99999 | 30.00",
            "non-numeric account   | Vendor  | abcde | abcde | 30.00",
            "empty amount          | Vendor  | 12345 | 12345 | ''",
            "non-numeric amount    | Vendor  | 12345 | 12345 | abc"
    })
    @DisplayName("ECP invalid classes: payment is rejected")
    void invalidPayments(String label, String payee, String account, String verify, String amount) {
        BillPayPage page = overview().goToBillPay()
                .fillPayee(payee, "1 Power St", "Chennai", "TN", "600001", "9123456780", account, verify)
                .pay(amount, fromAccount);

        assertFalse(page.isPaymentSuccessful(), label + " must not complete");
        assertTrue(page.isErrorShown(), label + " should show a validation error");
    }
}
