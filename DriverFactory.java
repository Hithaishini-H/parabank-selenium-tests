package com.parabank.utils;

import com.parabank.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/** Creates WebDriver instances. Selenium Manager (built into Selenium 4.6+) downloads the driver. */
public final class DriverFactory {

    private DriverFactory() {}

    public static WebDriver create() {
        switch (Config.BROWSER) {
            case "firefox": {
                FirefoxOptions o = new FirefoxOptions();
                if (Config.HEADLESS) o.addArguments("-headless");
                return new FirefoxDriver(o);
            }
            case "edge": {
                EdgeOptions o = new EdgeOptions();
                if (Config.HEADLESS) o.addArguments("--headless=new");
                return new EdgeDriver(o);
            }
            case "chrome":
            default: {
                ChromeOptions o = new ChromeOptions();
                o.addArguments("--window-size=1366,900", "--disable-notifications");
                if (Config.HEADLESS) o.addArguments("--headless=new");
                return new ChromeDriver(o);
            }
        }
    }
}
