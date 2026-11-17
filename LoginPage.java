package com.parabank.pages;

import com.parabank.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Home page (index.htm) hosting the login form. */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("input[value='Log In']");
    private static final By REGISTER_LINK = By.linkText("Register");
    private static final By ERROR_TITLE = By.cssSelector("#rightPanel h1.title");
    private static final By ERROR_MESSAGE = By.cssSelector("#rightPanel p.error, #rightPanel .error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(Config.url("/index.htm"));
        visible(USERNAME);
        return this;
    }

    /** Valid login -> lands on Accounts Overview. */
    public AccountsOverviewPage loginAs(String username, String password) {
        submit(username, password);
        return new AccountsOverviewPage(driver);
    }

    /** Invalid login -> stays on error page; returns this page object for error assertions. */
    public LoginPage loginExpectingFailure(String username, String password) {
        submit(username, password);
        return this;
    }

    private void submit(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    /** Waits until the error panel is shown (after a failed login) and returns the page. */
    public LoginPage waitForError() {
        visible(ERROR_MESSAGE);
        return this;
    }

    public RegisterPage goToRegister() {
        click(REGISTER_LINK);
        return new RegisterPage(driver);
    }

    public String getErrorTitle() {
        return text(ERROR_TITLE);
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }
}
