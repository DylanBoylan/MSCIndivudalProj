package com.tus.individual.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Top10ImsiIT {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String EMAIL = "test_network_management_engineer@example.com";
    private static final String PASSWORD = "SecurePass123!";

    @BeforeAll
    void setup() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://localhost:8081/index.html");
        loginSuccessfully();
    }

    @Test
    void testTop10ImsisQuery() {
        navigateToTop10Imsis();

        WebElement startDateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
        WebElement endDateField = driver.findElement(By.id("endDate"));
        WebElement fetchButton = driver.findElement(By.id("fetchImsis"));

        // ✅ Test Case: Entering valid dates
        String startTimeValue = "2020-01-01T00:00";
        String endTimeValue = "2020-12-12T00:00";

        startDateField.clear();
        startDateField.sendKeys(startTimeValue);
        endDateField.clear();
        endDateField.sendKeys(endTimeValue);

        // Trigger change events via JavaScript
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('startDate').value = arguments[0];", startTimeValue);
        js.executeScript("document.getElementById('endDate').value = arguments[0];", endTimeValue);

        
        // Click the fetch button again
        fetchButton.click();
        System.out.println("✅ Fetch button clicked successfully!");
        
        // ✅ Check that the "No failures found" message appears
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(errorMessage.getText().contains("✅ No failures found for the given time period."),
                   "❌ Expected message not displayed when no results are found.");
        System.out.println("✅ 'No failures found' message appeared as expected!");

        // ✅ Ensure the table remains hidden
        boolean isTableDisplayed = driver.findElement(By.id("tableWrapper")).isDisplayed();
        assertTrue(!isTableDisplayed, "❌ Table should remain hidden when no data is available.");
        System.out.println("✅ Table is correctly hidden when no failures are found.");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    void loginSuccessfully() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys(EMAIL);
        passwordField.sendKeys(PASSWORD);
        loginButton.click();

        dismissUnexpectedAlert();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
        assertTrue(driver.findElement(By.id("logoutButton")).isDisplayed(), "❌ Login failed.");
    }

    void dismissUnexpectedAlert() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert message: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("No alert present.");
        }
    }

    void navigateToTop10Imsis() {
        WebElement sidebar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sidebar")));
        WebElement top10ImsisNav = wait.until(ExpectedConditions.elementToBeClickable(By.id("top10imsisNav")));
        top10ImsisNav.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
    }
}
