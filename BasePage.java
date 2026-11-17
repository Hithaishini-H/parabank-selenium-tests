package com.parabank.pages;

import com.parabank.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/** Shared helpers + the left-hand navigation menu present on every logged-in page. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // Left menu (logged-in)
    private static final By OPEN_ACCOUNT_LINK = By.linkText("Open New Account");
    private static final By OVERVIEW_LINK = By.linkText("Accounts Overview");
    private static final By TRANSFER_LINK = By.linkText("Transfer Funds");
    private static final By BILLPAY_LINK = By.linkText("Bill Pay");
    private static final By LOGOUT_LINK = By.linkText("Log Out");

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.TIMEOUT);
    }

    // ---------- generic helpers ----------
    protected WebElement visible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void type(By by, String text) {
        WebElement e = visible(by);
        e.clear();
        if (text != null && !text.isEmpty()) {
            e.sendKeys(text);
        }
    }

    protected void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    protected String text(By by) {
        return visible(by).getText().trim();
    }

    protected boolean isDisplayed(By by) {
        List<WebElement> found = driver.findElements(by);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }

    protected void selectByVisibleText(By by, String text) {
        new Select(visible(by)).selectByVisibleText(text);
    }

    protected void selectByValue(By by, String value) {
        new Select(visible(by)).selectByValue(value);
    }

    public String getPageTitleHeading() {
        return text(By.cssSelector("#rightPanel h1.title, #rightPanel h1"));
    }

    // ---------- navigation ----------
    public AccountsOverviewPage goToAccountsOverview() {
        click(OVERVIEW_LINK);
        return new AccountsOverviewPage(driver);
    }

    public OpenAccountPage goToOpenNewAccount() {
        click(OPEN_ACCOUNT_LINK);
        return new OpenAccountPage(driver);
    }

    public TransferFundsPage goToTransferFunds() {
        click(TRANSFER_LINK);
        return new TransferFundsPage(driver);
    }

    public BillPayPage goToBillPay() {
        click(BILLPAY_LINK);
        return new BillPayPage(driver);
    }

    public LoginPage logout() {
        click(LOGOUT_LINK);
        return new LoginPage(driver);
    }

    public boolean isLoggedIn() {
        return isDisplayed(LOGOUT_LINK);
    }
}
