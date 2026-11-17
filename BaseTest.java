package com.parabank.base;

import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.LoginPage;
import com.parabank.utils.DriverFactory;
import com.parabank.utils.TestDataGenerator;
import com.parabank.utils.TestDataGenerator.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

/** Every UI test class extends this: fresh browser per test, quit afterwards. */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void startBrowser() {
        driver = DriverFactory.create();
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Registers a brand-new customer (auto-logged-in afterwards) and returns the credentials. */
    protected User registerNewUser() {
        User u = TestDataGenerator.newUser();
        new LoginPage(driver).open().goToRegister().fillAll(u).submitExpectingSuccess();
        return u;
    }

    protected AccountsOverviewPage overview() {
        return new AccountsOverviewPage(driver).goToAccountsOverview();
    }
}
