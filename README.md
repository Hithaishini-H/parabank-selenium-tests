<h1 align="center">🏦 ParaBank Test Automation</h1>

<p align="center">
  <b>Selenium WebDriver 4 · JUnit 5 · Page Object Model</b><br>
  Black-box (ECP, BVA) and white-box (CFG, cyclomatic complexity) testing of the ParaBank demo bank
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-11+-orange?logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Selenium-4.25-43B02A?logo=selenium" alt="Selenium">
  <img src="https://img.shields.io/badge/JUnit-5.10-25A162?logo=junit5" alt="JUnit 5">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven" alt="Maven">
</p>

---

## 📌 About the project
Automated UI test suite for the [ParaBank demo bank](https://parabank.parasoft.com/parabank). Test cases are designed with **Equivalence Class Partitioning (ECP)**, **Boundary Value Analysis (BVA)** and **white-box path analysis**.

## 🎯 Modules under test
| Module | Techniques |
|---|---|
| 🔐 **Login** | ECP (valid / invalid credentials, empty fields) |
| 📝 **Registration** | ECP, password-length probe |
| 🏧 **Open account** | Checking and savings, `$100` funding |
| 💸 **Transfer funds** | ECP, BVA (Min-1 to Max+1), end-to-end balance check |
| 🧾 **Bill pay** | ECP (valid payee, empty / mismatched / non-numeric fields) |
| 🧮 **`TransferValidator`** | White-box: control flow graph, **V(G) = 13**, 13 basis paths |

## 🛠️ Tech stack
- **Java** 11+
- **Selenium** 4.25 (Selenium Manager downloads the browser driver automatically)
- **JUnit** 5.10 (`@ParameterizedTest`, `@CsvSource`)
- **Maven**
- **Eclipse IDE**

## 📁 Project structure
```
src/main/java/com/parabank
  config/Config.java            base URL, browser, headless, timeout
  utils/DriverFactory.java      WebDriver creation
  utils/TestDataGenerator.java  unique users per run
  pages/                        Page objects: Login, Register, AccountsOverview,
                                OpenAccount, TransferFunds, BillPay
  whitebox/                     TransferValidator, CyclomaticComplexityCalculator
src/test/java/com/parabank
  base/BaseTest.java            driver lifecycle
  tests/                        LoginTest, RegistrationTest, TransferFundsTest,
                                BillPayTest, TransferValidatorWhiteBoxTest
docs/                           TEST_DESIGN.md, control flow graph (.dot / .png)
```
> **Design rule:** locators live only in the page classes. Tests call page methods and hold all assertions.

## 🚀 Getting started
```bash
git clone https://github.com/<your-username>/ParaBank-Selenium-Tests.git
cd ParaBank-Selenium-Tests
mvn test
```

### ▶️ Run options
| Goal | Command |
|---|---|
| All tests | `mvn test` |
| Headless | `mvn test -Dheadless=true` |
| One class | `mvn test -Dtest=TransferFundsTest` |
| Longer timeout | `mvn test -Dtimeout.seconds=30` |
| Other base URL | `mvn test -Dbase.url=http://localhost:8080/parabank` |

### 💻 Eclipse
1. `File > Import > Maven > Existing Maven Projects` and select the project folder.
2. Right-click the project, then `Maven > Update Project...` (tick **Force Update**).
3. Right-click `src/test/java`, then `Run As > JUnit Test`.

> `TransferValidatorWhiteBoxTest` needs no browser and runs offline.

## 🐞 Known ParaBank defects found
The tests log these as `KNOWN DEFECT` instead of failing, so the run stays green while the defects stay visible in the console.

| ID | Defect | Expected | Actual |
|---|---|---|---|
| **D-01** | Transfer of `0.00` | ✅ Rejected | ❌ Accepted |
| **D-02** | Transfer of balance + `0.01` | ✅ Rejected (overdraft) | ❌ Accepted |
| **D-03** | Transfer of `-5.00` | ✅ Rejected | ❌ Accepted |

Any other mismatch still fails the test, so new regressions are not hidden.

## 🩺 Troubleshooting
- ⏱️ **Timeouts:** the public demo server is sometimes slow or down. Open the site manually and retry, or raise `-Dtimeout.seconds`.
- 🔎 **Locator errors:** inspect the element in Chrome DevTools and update the `By` constants in `src/main/java/com/parabank/pages`.
- 🔄 **Odd data:** ParaBank's shared database is reset periodically, so each test registers a fresh user.

## 📚 Documentation
See [`docs/TEST_DESIGN.md`](docs/TEST_DESIGN.md) for the ECP tables, BVA values and the white-box analysis.
