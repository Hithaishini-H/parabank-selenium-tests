package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Accounts Overview (overview.htm) - also the landing page after login/registration. */
public class AccountsOverviewPage extends BasePage {

    private static final By ACCOUNT_ROWS = By.cssSelector("#accountTable tbody tr");
    private static final By ACCOUNT_TABLE = By.id("accountTable");
    private static final By WELCOME = By.cssSelector("#leftPanel p.smallText, #rightPanel h1.title");

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public AccountsOverviewPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_TABLE));
        return this;
    }

    /** Account ids = rows whose first cell contains a link (the final "Total" row has none). */
    public List<String> getAccountIds() {
        waitUntilLoaded();
        List<String> ids = new ArrayList<>();
        for (WebElement row : driver.findElements(ACCOUNT_ROWS)) {
            List<WebElement> links = row.findElements(By.cssSelector("td:first-child a"));
            if (!links.isEmpty()) {
                ids.add(links.get(0).getText().trim());
            }
        }
        return ids;
    }

    public BigDecimal getBalance(String accountId) {
        waitUntilLoaded();
        for (WebElement row : driver.findElements(ACCOUNT_ROWS)) {
            List<WebElement> links = row.findElements(By.cssSelector("td:first-child a"));
            if (!links.isEmpty() && links.get(0).getText().trim().equals(accountId)) {
                String raw = row.findElements(By.tagName("td")).get(1).getText();
                return new BigDecimal(raw.replaceAll("[^0-9.\\-]", ""));
            }
        }
        throw new IllegalStateException("Account " + accountId + " not found on overview");
    }

    public boolean isWelcomeVisible() {
        return isDisplayed(WELCOME);
    }
}
