package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transfer Funds (transfer.htm).
 * The account dropdowns are filled by AJAX after page load and the result is rendered into
 * #showResult / #showError, so every interaction here waits explicitly.
 */
public class TransferFundsPage extends BasePage {

    private static final By AMOUNT = By.id("amount");
    private static final By FROM = By.id("fromAccountId");
    private static final By TO = By.id("toAccountId");
    private static final By TRANSFER_BUTTON = By.cssSelector("input[value='Transfer']");

    private static final By RESULT_BLOCK = By.id("showResult");
    private static final By ERROR_BLOCK = By.id("showError");
    private static final By INLINE_ERROR = By.cssSelector("#rightPanel .error");
    private static final By AMOUNT_RESULT = By.id("amountResult");

    public TransferFundsPage(WebDriver driver) {
        super(driver);
    }

    private static List<String> optionTexts(WebDriver d, By by) {
        List<WebElement> found = d.findElements(by);
        if (found.isEmpty()) {
            return List.of();
        }
        return new Select(found.get(0)).getOptions().stream()
                .map(o -> o.getText().trim())
                .collect(Collectors.toList());
    }

    /** Waits until BOTH dropdowns contain at least one account. */
    public TransferFundsPage waitUntilReady() {
        wait.until(d -> !optionTexts(d, FROM).isEmpty() && !optionTexts(d, TO).isEmpty());
        return this;
    }

    public List<String> getFromAccounts() {
        waitUntilReady();
        return optionTexts(driver, FROM);
    }

    public List<String> getToAccounts() {
        waitUntilReady();
        return optionTexts(driver, TO);
    }

    public boolean areDropdownsPopulated() {
        try {
            waitUntilReady();
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public TransferFundsPage transfer(String amount, String fromAccountId, String toAccountId) {
        waitUntilReady();
        // make sure the specific accounts are already in the lists before selecting them
        wait.until(d -> optionTexts(d, FROM).contains(fromAccountId)
                && optionTexts(d, TO).contains(toAccountId));

        type(AMOUNT, amount);
        selectByVisibleText(FROM, fromAccountId);
        selectByVisibleText(TO, toAccountId);
        click(TRANSFER_BUTTON);

        // wait for ANY outcome: success, ajax error, or inline validation error
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(RESULT_BLOCK),
                ExpectedConditions.visibilityOfElementLocated(ERROR_BLOCK),
                ExpectedConditions.visibilityOfElementLocated(INLINE_ERROR)));
        return this;
    }

    public boolean isTransferSuccessful() {
        List<WebElement> block = driver.findElements(RESULT_BLOCK);
        return !block.isEmpty() && block.get(0).isDisplayed()
                && block.get(0).getText().toLowerCase().contains("transfer complete");
    }

    public boolean isErrorShown() {
        return isDisplayed(ERROR_BLOCK) || isDisplayed(INLINE_ERROR);
    }

    public String getTransferredAmountText() {
        return text(AMOUNT_RESULT);
    }
}
