package com.parabank.pages;

import com.parabank.config.Config;
import com.parabank.utils.TestDataGenerator.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

/** Customer registration form (register.htm). */
public class RegisterPage extends BasePage {

    private static final By FIRST_NAME = By.id("customer.firstName");
    private static final By LAST_NAME = By.id("customer.lastName");
    private static final By STREET = By.id("customer.address.street");
    private static final By CITY = By.id("customer.address.city");
    private static final By STATE = By.id("customer.address.state");
    private static final By ZIP = By.id("customer.address.zipCode");
    private static final By PHONE = By.id("customer.phoneNumber");
    private static final By SSN = By.id("customer.ssn");
    private static final By USERNAME = By.id("customer.username");
    private static final By PASSWORD = By.id("customer.password");
    private static final By CONFIRM = By.id("repeatedPassword");
    private static final By REGISTER_BUTTON = By.cssSelector("input[value='Register']");

    private static final By FIELD_ERRORS = By.cssSelector("#rightPanel span.error, #customerForm span.error");
    private static final By SUCCESS_HEADING = By.cssSelector("#rightPanel h1.title");
    private static final By SUCCESS_TEXT = By.cssSelector("#rightPanel p");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage open() {
        driver.get(Config.url("/register.htm"));
        visible(FIRST_NAME);
        return this;
    }

    // ---- fluent field setters (needed so ECP tests can blank out one field) ----
    public RegisterPage firstName(String v) { type(FIRST_NAME, v); return this; }
    public RegisterPage lastName(String v)  { type(LAST_NAME, v);  return this; }
    public RegisterPage street(String v)    { type(STREET, v);     return this; }
    public RegisterPage city(String v)      { type(CITY, v);       return this; }
    public RegisterPage state(String v)     { type(STATE, v);      return this; }
    public RegisterPage zip(String v)       { type(ZIP, v);        return this; }
    public RegisterPage phone(String v)     { type(PHONE, v);      return this; }
    public RegisterPage ssn(String v)       { type(SSN, v);        return this; }
    public RegisterPage username(String v)  { type(USERNAME, v);   return this; }
    public RegisterPage password(String v)  { type(PASSWORD, v);   return this; }
    public RegisterPage confirm(String v)   { type(CONFIRM, v);    return this; }

    public RegisterPage fillAll(User u) {
        return firstName(u.firstName).lastName(u.lastName).street(u.street).city(u.city)
                .state(u.state).zip(u.zip).phone(u.phone).ssn(u.ssn)
                .username(u.username).password(u.password).confirm(u.password);
    }

    /** Submit and expect success (Accounts/Welcome page). */
    public AccountsOverviewPage submitExpectingSuccess() {
        click(REGISTER_BUTTON);
        visible(SUCCESS_HEADING);
        return new AccountsOverviewPage(driver);
    }

    /**
     * Submit and wait for the outcome: either validation errors appear on the form,
     * or registration succeeded (user is logged in).
     */
    public RegisterPage submit() {
        click(REGISTER_BUTTON);
        wait.until(d -> !getFieldErrors().isEmpty() || isLoggedIn());
        return this;
    }

    public RegisterPage registerUser(User u) {
        fillAll(u);
        click(REGISTER_BUTTON);
        return this;
    }

    // ---- result inspection ----
    public List<String> getFieldErrors() {
        return driver.findElements(FIELD_ERRORS).stream()
                .filter(e -> e.isDisplayed() && !e.getText().isBlank())
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public boolean hasError(String expectedFragment) {
        return getFieldErrors().stream()
                .anyMatch(s -> s.toLowerCase().contains(expectedFragment.toLowerCase()));
    }

    public String getSuccessHeading() {
        return text(SUCCESS_HEADING);
    }

    public String getSuccessMessage() {
        return text(SUCCESS_TEXT);
    }
}
