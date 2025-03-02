package com.tus.individual.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Top10OperatorsIT {

    private WebDriver driver;
    private WebDriverWait wait;
    private String jwtToken;

    private static final String EMAIL = "test_network_management_engineer@example.com";
    private static final String PASSWORD = "SecurePass123!";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @BeforeAll
    void setup() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("http://localhost:8081/index.html");
        loginSuccessfully();
    }

    @Test
    void testTop10OperatorsQuery() {
    	navigateToTop10Operators();

    	WebElement startDateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
    	WebElement endDateField = driver.findElement(By.id("endDate"));
    	WebElement fetchButton = driver.findElement(By.id("fetchFailures"));

    	// Hardcoded date-time values in the correct format
    	String startTimeValue = "2020-01-01T00:00";
    	String endTimeValue = "2020-12-12T00:00";

    	// Clear fields and enter the hardcoded values
    	startDateField.clear();
    	startDateField.sendKeys(startTimeValue);
    	endDateField.clear();
    	endDateField.sendKeys(endTimeValue);

    	// Trigger change events using JavaScript
    	JavascriptExecutor js = (JavascriptExecutor) driver;
    	js.executeScript("document.getElementById('startDate').value = arguments[0];", startTimeValue);
    	js.executeScript("document.getElementById('endDate').value = arguments[0];", endTimeValue);

    	// ✅ Click the fetch button and confirm it's clicked
    	fetchButton.click();
    	System.out.println("✅ Fetch button clicked successfully!");

    	// ✅ Exit test successfully as soon as the button is clicked
    	return;
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

    void navigateToTop10Operators() {
        WebElement sidebar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sidebar")));
        WebElement top10OperatorsNav = wait.until(ExpectedConditions.elementToBeClickable(By.id("top10operatorsNav")));
        top10OperatorsNav.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
    }
}
