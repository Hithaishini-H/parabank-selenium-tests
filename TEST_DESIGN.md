# ParaBank – Formal Test Design

Application under test: https://parabank.parasoft.com/parabank (Parasoft demo bank)

## 1. Equivalence Class Partitioning (ECP)

### 1.1 Login (username × password)
| ID | Class | Username | Password | Expected |
|----|-------|----------|----------|----------|
| V1 | Valid | registered | correct | Accounts Overview, Log Out visible |
| I1 | Invalid | unknown | any | error "could not be verified" |
| I2 | Invalid | registered | wrong | error |
| I3 | Invalid | empty | any | error "Please enter a username and password" |
| I4 | Invalid | any | empty | error |

### 1.2 Registration
| ID | Class | Condition | Expected |
|----|-------|-----------|----------|
| V1 | Valid | all 10 fields filled, password = confirmation, unused username | "Your account was created successfully" |
| I1–I9 | Invalid | one required field blank (first name, last name, address, city, state, zip, SSN, username, password) | field-specific "… is required." |
| I10 | Invalid | confirmation blank | "Password confirmation is required." |
| I11 | Invalid | confirmation ≠ password | "Passwords did not match." |
| I12 | Invalid | username already exists | "This username already exists." |

### 1.3 Transfer amount
| Class | Range / form | Expected |
|-------|--------------|----------|
| Valid | number, 0.01 ≤ x ≤ balance, ≤ 2 decimals | Transfer Complete |
| Invalid | empty | error |
| Invalid | non-numeric (abc, $$$) | error |
| Invalid | x ≤ 0 | error |
| Invalid | x > balance | rejected (specification oracle) |

### 1.4 Bill Pay
Valid: full payee details, matching account numbers, positive numeric amount.
Invalid: empty payee, account ≠ verify, non-numeric account, empty / non-numeric amount.

## 2. Boundary Value Analysis (BVA)

### 2.1 UI – transfer amount, range [0.01, source balance]
| Point | Value | Expected |
|-------|-------|----------|
| Min-1 | 0.00 | reject |
| Min | 0.01 | accept |
| Min+1 | 0.02 | accept |
| Nominal | 50.00 | accept |
| Max-1 | balance − 0.01 | accept |
| Max | balance | accept |
| Max+1 | balance + 0.01 | reject |

### 2.2 Unit – `TransferValidator`, range [0.01, 100000.00]
| Point | Value | Result |
|-------|-------|--------|
| Min-1 | 0.00 | NON_POSITIVE |
| Min | 0.01 | OK |
| Min+1 | 0.02 | OK |
| Nominal | 50000.00 | OK |
| Max-1 | 99999.99 | OK |
| Max | 100000.00 | OK |
| Max+1 | 100000.01 | EXCEEDS_LIMIT |

## 3. White-Box Testing – `TransferValidator.validate`

Control Flow Graph: `docs/TransferValidator_CFG.png` (source: `TransferValidator_CFG.dot`).

**Cyclomatic complexity**
- Predicate nodes P = 12 (P1 `amount==null`, P2 `isEmpty`, P3 `catch NumberFormatException`, P4 `signum<=0`, P5 `scale>2`, P6 `>MAX`, P7–P10 four conditions of the account check, P11 same account, P12 `>balance`).
- V(G) = P + 1 = **13**
- Cross-check by edges/nodes: V(G) = E − N + 2. The test `cyclomaticComplexityIs13` verifies this automatically from the source file.

**Basis paths (13 independent paths, one test each)**
| Path | Decision outcomes | Input | Result |
|------|-------------------|-------|--------|
| 1 | P1=T | null | EMPTY_AMOUNT |
| 2 | P1=F,P2=T | "   " | EMPTY_AMOUNT |
| 3 | P3=T | abc | NOT_NUMERIC |
| 4 | P4=T | 0 | NON_POSITIVE |
| 5 | P5=T | 10.555 | TOO_MANY_DECIMALS |
| 6 | P6=T | 100000.01 | EXCEEDS_LIMIT |
| 7 | P7=T | from=null | MISSING_ACCOUNT |
| 8 | P8=T | from blank | MISSING_ACCOUNT |
| 9 | P9=T | to=null | MISSING_ACCOUNT |
| 10 | P10=T | to blank | MISSING_ACCOUNT |
| 11 | P11=T | from=to | SAME_ACCOUNT |
| 12 | P12=T | 100.01, balance 100.00 | INSUFFICIENT_FUNDS |
| 13 | all F | 50.00 | OK |

All 13 paths = 100 % statement, branch and basis-path coverage.
