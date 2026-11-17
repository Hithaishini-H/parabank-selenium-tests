package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.LoginPage;
import com.parabank.utils.TestDataGenerator.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box ECP for the login form.
 *  V1 registered + correct password -> success
 *  I1 unknown user | I2 wrong password | I3 empty username | I4 empty password | I5 both empty
 */
class LoginTest extends BaseTest {

    @Test
    @DisplayName("ECP V1: valid username + valid password logs in")
    void verifyValidLogin() {
        User u = registerNewUser();
        LoginPage login = new AccountsOverviewPage(driver).logout();

        AccountsOverviewPage page = login.loginAs(u.username, u.password).waitUntilLoaded();

        assertTrue(page.isLoggedIn(), "Log Out link should be visible after valid login");
    }

    @ParameterizedTest(name = "[{index}] user=''{0}'' password=''{1}''")
    @CsvSource({
            "ghost_user_does_not_exist, Passw0rd!",   // I1 unknown user
            "'', ''",                                 // I5 both empty
            "'', Passw0rd!",                          // I3 empty username
            "someuser, ''"                            // I4 empty password
    })
    @DisplayName("ECP invalid classes: login is rejected with the Error page")
    void verifyInvalidLogin(String username, String password) {
        LoginPage login = new LoginPage(driver).open();

        login.loginExpectingFailure(username, password).waitForError();

        // The wording differs per case ("could not be verified" / "Please enter a username
        // and password" / "internal error" on the busy demo server), so assert the common
        // contract: error panel shown, Error heading, and the user is NOT logged in.
        assertTrue(login.isErrorDisplayed(), "An error message should be displayed");
        assertTrue(login.getErrorTitle().toLowerCase().contains("error"),
                "Heading should be 'Error!' but was: " + login.getErrorTitle());
        assertFalse(login.isLoggedIn(), "User must not be logged in");
    }

    @Test
    @DisplayName("ECP I2: registered user + wrong password is rejected")
    void verifyWrongPasswordRejected() {
        User u = registerNewUser();
        LoginPage login = new AccountsOverviewPage(driver).logout();

        login.loginExpectingFailure(u.username, "totally-wrong").waitForError();

        assertTrue(login.isErrorDisplayed());
        assertFalse(login.isLoggedIn());
    }
}
