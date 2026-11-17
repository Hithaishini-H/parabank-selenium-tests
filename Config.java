package com.parabank.config;

import java.time.Duration;

/**
 * Central configuration. Override from Eclipse via
 * Run Configurations > Arguments > VM arguments, e.g.:
 *   -Dbrowser=firefox -Dheadless=true -Dbase.url=http://localhost:8080/parabank
 */
public final class Config {

    private Config() {}

    public static final String BASE_URL =
            System.getProperty("base.url", "https://parabank.parasoft.com/parabank");

    public static final String BROWSER =
            System.getProperty("browser", "chrome").toLowerCase();

    public static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "false"));

    public static final Duration TIMEOUT =
            Duration.ofSeconds(Long.getLong("timeout.seconds", 15L));

    public static String url(String path) {
        return BASE_URL + (path.startsWith("/") ? path : "/" + path);
    }
}
