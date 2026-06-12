# OrangeHRM — Selenium + TestNG Automation Framework

A **production-ready**, interview-ready test automation framework built with
**Java · Selenium WebDriver · TestNG · Maven**, targeting the OrangeHRM demo application.

---

## Project Structure

```
EventManagementFramework/
│
├── pom.xml                            ← Maven dependencies & Surefire plugin config
├── testng.xml                         ← TestNG suite: groups, parallel, listeners
│
├── src/
│   ├── main/java/com/eventmgmt/
│   │   ├── base/
│   │   │   ├── BasePage.java          ← Parent of all Page Objects; low-level helpers
│   │   │   └── BaseTest.java          ← Parent of all Tests; @Before/@After lifecycle
│   │   ├── pages/
│   │   │   ├── LoginPage.java         ← Page Object for OrangeHRM login page
│   │   │   ├── DashboardPage.java     ← Page Object for OrangeHRM dashboard + logout
│   │   │   └── RegistrationPage.java  ← Legacy page object retained for reference
│   │   └── utils/
│   │       ├── ConfigReader.java      ← Singleton: reads config.properties
│   │       ├── DriverFactory.java     ← ThreadLocal WebDriver factory
│   │       ├── ExcelReader.java       ← Apache POI Excel reader for @DataProvider
│   │       ├── ExtentReportManager.java ← Extent HTML report (per-thread)
│   │       ├── ScreenshotUtil.java    ← Captures screenshots (file + Base64)
│   │       ├── TestListener.java      ← ITestListener: auto-screenshot on failure
│   │       └── WaitUtil.java          ← Explicit wait helpers
│   │
│   └── test/
│       ├── java/com/eventmgmt/tests/
│       │   ├── LoginTest.java         ← OrangeHRM login validation smoke test
│       │   ├── DashboardTest.java     ← OrangeHRM dashboard validation smoke test
│       │   └── LogoutTest.java        ← OrangeHRM logout smoke test
│       │
│       └── resources/
│           ├── config/
│           │   ├── config.properties  ← URLs, browser, timeouts, file paths
│           │   └── log4j2.xml         ← Log4j2 console + rolling file config
│           ├── screenshots/           ← Auto-saved failure screenshots
│           └── testdata/
│               ├── LoginTestData.xlsx        ← Login DDT data (8 rows)
│               ├── RegistrationTestData.xlsx ← Registration DDT data (6 rows)
│               └── BookingTestData.xlsx      ← Booking DDT data (7 rows)
│
├── test-output/
│   └── ExtentReport.html              ← Generated HTML report (open in browser)
└── logs/
    └── automation.log                 ← Rolling log file
```

---

## Technology Stack

| Tool                  | Version  | Purpose                                    |
|-----------------------|----------|--------------------------------------------|
| Java                  | 17+      | Core language                              |
| Selenium WebDriver    | 4.18.1   | Browser automation                         |
| TestNG                | 7.9.0    | Test framework (annotations, assertions)   |
| Maven                 | 3.8+     | Build tool & dependency management         |
| WebDriverManager      | 6.1.0    | Auto-downloads correct browser drivers     |
| ExtentReports         | 5.1.1    | Interactive HTML test reports              |
| Apache POI            | 5.2.5    | Read Excel (.xlsx) test data files         |
| Log4j2                | 2.23.1   | Structured logging to console and file     |
| Commons IO            | 2.15.1   | File utilities (screenshot saving)         |
| OpenCSV               | 5.9      | CSV file support (optional extension)      |

---

## Prerequisites

1. **Java 17+** — `java -version` to confirm
2. **Maven 3.8+** — `mvn -version` to confirm
3. **Chrome / Firefox / Edge** — latest stable version
4. **IDE** — IntelliJ IDEA or Eclipse (optional, for local development)

---

## Setup Instructions

### 1 — Clone / Download the project

```bash
git clone https://github.com/your-org/EventManagementFramework.git
cd EventManagementFramework
```

### 2 — Configure the target application

Edit `src/test/resources/config/config.properties`:

```properties
base.url=https://opensource-demo.orangehrmlive.com/
login.url=https://opensource-demo.orangehrmlive.com/
register.url=https://opensource-demo.orangehrmlive.com/
booking.url=https://opensource-demo.orangehrmlive.com/

browser=chrome          # chrome | firefox | edge
headless=false          # true for CI

valid.username=Admin
valid.password=admin123
```

### 3 — Install dependencies

```bash
mvn clean install -DskipTests
```

---

## Running Tests

### Run the smoke suite

```bash
mvn test
```

### Run in headless mode (CI/CD)

```bash
mvn test -Dheadless=true
```

### Run a specific test class

```bash
mvn test -Dtest=LoginTest
mvn test -Dtest=DashboardTest
mvn test -Dtest=LogoutTest
```

### Run a specific test method

```bash
mvn test -Dtest=LoginTest#testValidLogin
```

### Change browser at runtime

```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

---

## Reports & Artefacts

| Artefact               | Location                                          |
|------------------------|---------------------------------------------------|
| Extent HTML Report     | `test-output/ExtentReport.html`                   |
| TestNG Default Report  | `test-output/index.html`                          |
| Failure Screenshots    | `src/test/resources/screenshots/`                 |
| Log File               | `logs/automation.log`                             |

Open `test-output/ExtentReport.html` in any browser for a full interactive report.

---

## Framework Design Patterns

### Page Object Model (POM)
- Every page of the application has a dedicated class under `pages/`
- Element locators are declared as `@FindBy` fields
- Public methods represent user actions (e.g. `login()`, `registerUser()`)
- Tests never touch `WebElement` directly

### ThreadLocal WebDriver (Parallel-safe)
- `DriverFactory` stores one `WebDriver` per thread in `ThreadLocal<WebDriver>`
- Enables parallel test execution without browser collisions
- `DriverFactory.quitDriver()` removes the driver cleanly after each test

### Data-Driven Testing
- `ExcelReader` reads `.xlsx` files using Apache POI
- `@DataProvider` in each test class converts rows to `Object[][]`
- Edit the `.xlsx` files to add/change test data without touching code

### Screenshot on Failure
- `TestListener` implements `ITestListener.onTestFailure()`
- Calls `ScreenshotUtil` to capture a Base64 screenshot
- Screenshot is embedded in the Extent HTML report AND saved to disk

### Extent Reports (Rich HTML)
- `ExtentReportManager` is a singleton, thread-safe
- `ExtentTest` nodes are stored in `ThreadLocal` for parallel safety
- System info (OS, Java, Browser, URL) shown at top of report

---

## Extending the Framework

### Add a new page
1. Create `src/main/java/com/eventmgmt/pages/DashboardPage.java`
2. Extend `BasePage`, add `@FindBy` locators, add action methods

### Add a new test class
1. Create `src/test/java/com/eventmgmt/tests/DashboardTest.java`
2. Extend `BaseTest`
3. Add `@Test` methods, assign groups
4. Add the class to `testng.xml`

### Add new test data
1. Open the relevant `.xlsx` file in Excel
2. Append rows (keep column headers identical)
3. Run the data-driven test — new rows are automatically picked up

---

## Interview Talking Points

| Area                      | Implementation Detail                                               |
|---------------------------|---------------------------------------------------------------------|
| Design Pattern            | Page Object Model — separation of locators and actions              |
| Parallel Execution        | `ThreadLocal<WebDriver>` + `parallel="methods"` in testng.xml       |
| Data-Driven Testing       | Apache POI + `@DataProvider` — zero code changes for new scenarios  |
| Failure Handling          | `ITestListener` + automatic Base64 screenshot → Extent report       |
| Explicit Waits            | Centralised `WaitUtil` — no `Thread.sleep()` anywhere               |
| Config Management         | Singleton `ConfigReader` — one change point for env switches        |
| Reporting                 | ExtentReports 5 (Spark) — interactive, filterable HTML report       |
| Logging                   | Log4j2 with console + rolling file appenders                        |
| Browser Flexibility       | `DriverFactory` supports Chrome, Firefox, Edge via config           |
| CI Readiness              | `headless=true` flag + Maven Surefire + exit code on failure        |
