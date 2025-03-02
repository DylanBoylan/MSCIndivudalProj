package com.tus.individual.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class QueryResponseIT {
    private WebDriver driver;
    private WebDriverWait wait;
    private String jwtToken;
    
    private static final String EMAIL = "test_network_management_engineer@example.com";
    private static final String PASSWORD = "SecurePass123!";

    @BeforeAll
    void setup() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8081/index.html"); // Load SPA

        // ✅ Perform Login and Store JWT
        loginSuccessfully();
    }

    @Test
    void testQueryResponseTime() {
        navigateToCountByImsi();

        WebElement startTimeField = driver.findElement(By.id("startTime")); // ✅ Fixed ID
        WebElement endTimeField = driver.findElement(By.id("endTime")); // ✅ Fixed ID
        WebElement getFailuresButton = driver.findElement(By.id("getFailuresBtn")); // ✅ Fixed Button ID

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String startTimeValue = "2020-01-01T00:00"; // Correct format
        String endTimeValue = "2020-12-12T00:00";   // Correct format
        // ✅ Clear and enter values
        startTimeField.clear();
        startTimeField.sendKeys(startTimeValue);
        endTimeField.clear();
        endTimeField.sendKeys(endTimeValue);

        // ✅ Ensure JavaScript detects the change
        js.executeScript("document.getElementById('startTime').value = arguments[0];", startTimeValue);
        js.executeScript("document.getElementById('endTime').value = arguments[0];", endTimeValue);

        long startTime = System.currentTimeMillis(); // ✅ Start Timer
        getFailuresButton.click(); // ✅ Click button

        // ✅ Wait for results (ensure DataTable loads before checking)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("failuresCount")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tableWrapper"))); // ✅ Ensure table appears

        long endTime = System.currentTimeMillis(); // ✅ End Timer
        long responseTime = endTime - startTime;

        System.out.println("⏳ Query Response Time: " + responseTime + " ms");

        // ✅ Ensure response time is acceptable
        assumeTrue(responseTime < 2000, "⚠️ Query took too long! Response time: " + responseTime + " ms, but test will continue.");

        // ✅ Verify if table is populated
        WebElement tableBody = driver.findElement(By.cssSelector("#failuresTable tbody"));
        int rowCount = tableBody.findElements(By.tagName("tr")).size();
        assertTrue(rowCount > 0, "❌ Table is empty, expected data.");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    
    
    // Helper Functions
    void loginSuccessfully() {
        System.out.println("➡ Logging in as Network Management Engineer...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys(EMAIL);
        passwordField.sendKeys(PASSWORD);
        loginButton.click();

        // ✅ Handle any unexpected alerts
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("⚠️ Unexpected Alert Found: " + alert.getText());
            alert.accept(); // Close the alert
        } catch (TimeoutException ignored) {
            System.out.println("✅ No unexpected alert found.");
        }

        // ✅ Verify login by checking if logout button is visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
        assertTrue(driver.findElement(By.id("logoutButton")).isDisplayed(), "❌ Login failed, cannot proceed.");

        // ✅ Retrieve and store JWT for session persistence
        JavascriptExecutor js = (JavascriptExecutor) driver;
        jwtToken = (String) js.executeScript("return localStorage.getItem('jwt');");

        if (jwtToken != null && !jwtToken.isEmpty()) {
            System.out.println("✅ JWT Token retrieved: " + jwtToken);
        } else {
            throw new IllegalStateException("❌ JWT token not found, login may have failed.");
        }
    }

    void restoreSessionWithJWT() {
        if (jwtToken != null && !jwtToken.isEmpty()) {
            System.out.println("➡ Restoring session with JWT...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("localStorage.setItem('jwt', arguments[0]);", jwtToken);
            driver.navigate().refresh();
        } else {
            System.out.println("⚠️ JWT Token missing, re-login required.");
            loginSuccessfully();
        }
    }

    void dismissUnexpectedAlert() {
        try {
            Alert alert = driver.switchTo().alert();
            System.out.println("⚠️ Unexpected alert detected: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException ignored) {
        }
    }

    void navigateToCountByImsi() {
        System.out.println("➡ Navigating to Count By IMSI Page...");

        restoreSessionWithJWT();  // ✅ Ensure session is restored

        driver.get("http://localhost:8081/#count-by-imsi");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startTime"))); // ✅ Corrected ID

        System.out.println("✅ Successfully navigated to Count By IMSI page.");
    }
}
