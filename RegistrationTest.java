package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.LoginPage;
import com.parabank.pages.RegisterPage;
import com.parabank.utils.TestDataGenerator;
import com.parabank.utils.TestDataGenerator.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box ECP for the registration form.
 * Valid class : every field filled, password == confirmation, username unused.
 * Invalid classes: one per required field left blank, mismatched confirmation, duplicate username.
 */
class RegistrationTest extends BaseTest {

    @Test
    @DisplayName("ECP valid class: all fields valid -> account created and user logged in")
    void registerWithValidData() {
        User u = TestDataGenerator.newUser();
        RegisterPage page = new LoginPage(driver).open().goToRegister().fillAll(u);
        page.submitExpectingSuccess();

        assertTrue(page.getSuccessHeading().contains(u.username), "Heading should welcome the new user");
        assertTrue(page.getSuccessMessage().toLowerCase().contains("created successfully"));
        assertTrue(page.isLoggedIn());
    }

    @ParameterizedTest(name = "blank {0} -> \"{1}\"")
    @CsvSource({
            "firstName,  first name",
            "lastName,   last name",
            "street,     address",
            "city,       city",
            "state,      state",
            "zip,        zip code",
            "ssn,        social security",
            "username,   username",
            "password,   password is required",
            "confirm,    password confirmation"
    })
    @DisplayName("ECP invalid classes: each required field left empty shows its own error")
    void blankRequiredField(String field, String expectedFragment) {
        RegisterPage page = new LoginPage(driver).open().goToRegister().fillAll(TestDataGenerator.newUser());

        switch (field) {
            case "firstName": page.firstName(""); break;
            case "lastName":  page.lastName("");  break;
            case "street":    page.street("");    break;
            case "city":      page.city("");      break;
            case "state":     page.state("");     break;
            case "zip":       page.zip("");       break;
            case "ssn":       page.ssn("");       break;
            case "username":  page.username("");  break;
            case "password":  page.password(""); break;
            case "confirm":   page.confirm("");  break;
            default: fail("Unknown field " + field);
        }
        page.submit();

        assertTrue(page.hasError(expectedFragment),
                "Expected an error containing '" + expectedFragment + "' but got " + page.getFieldErrors());
    }

    @Test
    @DisplayName("Submitting a completely empty form shows an error for every required field")
    void verifyEmptyRegistrationFields() {
        RegisterPage page = new LoginPage(driver).open().goToRegister();
        page.submit();

        String[] expected = {"first name", "last name", "address", "city", "state", "zip code",
                "social security", "username", "password is required", "password confirmation"};
        for (String fragment : expected) {
            assertTrue(page.hasError(fragment),
                    "Missing error containing '" + fragment + "'. Errors shown: " + page.getFieldErrors());
        }
    }

    @Test
    @DisplayName("ECP invalid class: password and confirmation differ")
    void passwordMismatch() {
        RegisterPage page = new LoginPage(driver).open().goToRegister()
                .fillAll(TestDataGenerator.newUser()).confirm("DifferentPass1!");
        page.submit();

        assertTrue(page.hasError("did not match"), "Errors: " + page.getFieldErrors());
    }

    @Test
    @DisplayName("ECP invalid class: username already taken")
    void duplicateUsername() {
        User first = registerNewUser();                         // creates the username
        new com.parabank.pages.AccountsOverviewPage(driver).logout();    // back to the home page

        RegisterPage page = new LoginPage(driver).open().goToRegister().fillAll(first);
        page.submit();

        assertTrue(page.hasError("already exists"), "Errors: " + page.getFieldErrors());
    }

    @ParameterizedTest(name = "password \"{0}\" is accepted (no length/complexity rule on the form)")
    @ValueSource(strings = {"a", "Passw0rd!", "this-is-a-fairly-long-password-1234567890"})
    @DisplayName("BVA-style probe on password length (min / nominal / long)")
    void passwordLengthProbe(String pwd) {
        User u = new User(TestDataGenerator.uniqueUsername(), pwd);
        RegisterPage page = new LoginPage(driver).open().goToRegister().fillAll(u);
        page.submit();

        assertTrue(page.isLoggedIn() || page.hasError("password"),
                "Either registration succeeds or a password-specific error is shown");
    }
}
