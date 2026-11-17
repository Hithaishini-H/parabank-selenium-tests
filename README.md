# ParaBank Automation (Selenium 4 + JUnit 5 + POM)

## Import into Eclipse (easiest)
1. `File > Import > General > Existing Projects into Workspace`
2. Choose **Select archive file** -> pick `ParaBank-Selenium-Tests.zip` -> Finish.
3. Right-click the project -> `Maven > Update Project...` -> tick Force Update -> OK (needs internet).
Requires a JDK 11+ (Window > Preferences > Java > Installed JREs) and Chrome.

## Run
- Right-click `TransferValidatorWhiteBoxTest` → `Run As > JUnit Test` (no browser, runs offline logic).
- Right-click `src/test/java` → `Run As > JUnit Test` for everything.
- Maven: `mvn test` · headless: `mvn test -Dheadless=true` · Firefox: `-Dbrowser=firefox`.
- In Eclipse pass options under `Run Configurations > Arguments > VM arguments`, e.g. `-Dheadless=true`.

## Layout
```
src/main/java/com/parabank
  config/Config.java            base URL, browser, headless, timeout
  utils/DriverFactory.java      WebDriver creation
  utils/TestDataGenerator.java  unique users
  pages/                        POM: Login, Register, AccountsOverview, OpenAccount, TransferFunds, BillPay
  whitebox/                     TransferValidator + CyclomaticComplexityCalculator
src/test/java/com/parabank
  base/BaseTest.java            driver lifecycle (@BeforeEach/@AfterEach)
  tests/                        Login, Registration, TransferFunds, BillPay, TransferValidatorWhiteBox
docs/                           TEST_DESIGN.md, CFG png/dot
```

## Troubleshooting
- Tests fail with Timeout: open the page manually; the demo server is sometimes slow or down. Raise `-Dtimeout.seconds=30`.
- Failed assertion on a boundary row (e.g. Max+1 accepted) = ParaBank differs from the spec; report as a finding.
- Locator errors: inspect the element in Chrome DevTools and update the `By` constants in `src/main/java/com/parabank/pages`.
