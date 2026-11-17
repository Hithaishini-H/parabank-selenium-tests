package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/** Open New Account (openaccount.htm). New account is funded with $100 from an existing one. */
public class OpenAccountPage extends BasePage {

    public enum Type { CHECKING, SAVINGS }

    private static final By TYPE_SELECT = By.id("type");
    private static final By FROM_SELECT = By.id("fromAccountId");
    private static final By OPEN_BUTTON = By.cssSelector("input[value='Open New Account']");
    private static final By RESULT_HEADING = By.cssSelector("#openAccountResult h1.title");
    private static final By NEW_ACCOUNT_ID = By.id("newAccountId");

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    /** The "from" dropdown is populated by JavaScript, so wait until it has options. */
    public OpenAccountPage waitUntilReady() {
        wait.until(d -> new Select(d.findElement(FROM_SELECT)).getOptions().size() > 0);
        return this;
    }

    public OpenAccountPage selectType(Type type) {
        selectByVisibleText(TYPE_SELECT, type.name().charAt(0) + type.name().substring(1).toLowerCase());
        return this;
    }

    /** Opens an account and returns the new account number. */
    public String openAccount(Type type) {
        waitUntilReady().selectType(type);
        click(OPEN_BUTTON);
        wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_HEADING));
        return text(NEW_ACCOUNT_ID);
    }

    public String getResultHeading() {
        return text(RESULT_HEADING);
    }
}
