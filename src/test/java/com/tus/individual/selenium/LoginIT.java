package com.tus.individual.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class LoginIT {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 10s timeout
        driver.get("http://localhost:8081"); // Load SPA home page
    }

    @Test
    void testLoginSuccess() {
        System.out.println("➡ Testing valid login...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys("admin@networksys.com");
        passwordField.sendKeys("Admin123!");
        loginButton.click();

        // Handle alert
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException ignored) {
        	fail("Popup never appeared.");
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
        assertTrue(driver.findElement(By.id("logoutButton")).isDisplayed(), "❌ Logout button not found, login might have failed.");
    }

    @Test
    void testInvalidLoginWrongPassword() {
        System.out.println("➡ Testing login with wrong password...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys("admin@networksys.com");
        passwordField.sendKeys("WrongPassword123!");
        loginButton.click();

        System.out.println("➡ Login button clicked, checking for alert...");

        // ✅ Handle unexpected alert
        String alertText = "";
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText();
            System.out.println("✅ Alert detected: " + alertText);
            alert.accept(); // Close the alert
        } catch (TimeoutException e) {
            System.out.println("⚠ No alert detected.");
        }

        // ✅ Ensure the error message is correct
        assertTrue(alertText.contains("Login Failed"), 
                "❌ Expected 'Login Failed' in alert.");

        // ✅ Ensure the login button is still visible (user is not redirected)
        boolean loginStillVisible = driver.findElements(By.id("loginButton")).size() > 0;
        assertTrue(loginStillVisible, "❌ User was redirected despite wrong password.");
    }


    @Test
    void testEmptyFields() {
        System.out.println("➡ Testing login with empty fields...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginButton")));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        loginButton.click(); // Click login with empty fields

        System.out.println("➡ Login button clicked, checking for alert...");

        // ✅ Handle unexpected alert
        String alertText = "";
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText();
            System.out.println("✅ Alert detected: " + alertText);
            alert.accept(); // Close the alert
        } catch (TimeoutException e) {
            System.out.println("⚠ No alert detected.");
        }

        // ✅ Ensure the error message is correct
        assertTrue(alertText.contains("Email is required") || alertText.contains("Password is required"), 
                "❌ Expected 'Email is required' or 'Password is required' in alert.");

        // ✅ Ensure login button is still visible (user is not redirected)
        boolean loginStillVisible = driver.findElements(By.id("loginButton")).size() > 0;
        assertTrue(loginStillVisible, "❌ User was redirected despite empty fields.");
    }


    @Test
    void testInvalidEmailFormat() {
        System.out.println("➡ Testing login with invalid email format...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys("invalidemail"); // Invalid email format
        passwordField.sendKeys("Admin123!");
        loginButton.click();

        System.out.println("➡ Login button clicked, checking for alert...");

        // ✅ Handle the alert if it appears
        String alertText = "";
        try {
            wait.until(ExpectedConditions.alertIsPresent()); // Wait for alert
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText();
            System.out.println("✅ Alert detected: " + alertText);
            alert.accept(); // Close the alert
        } catch (TimeoutException e) {
            System.out.println("⚠ No alert detected.");
        }

        // ✅ Verify that the error message was about invalid email format
        assertTrue(alertText.contains("Invalid email format"), "❌ Expected 'Invalid email format' in alert.");

        // ✅ Ensure the login button is still visible (user is not redirected)
        boolean loginStillVisible = driver.findElements(By.id("loginButton")).size() > 0;
        assertTrue(loginStillVisible, "❌ User was redirected despite invalid email.");
    }


    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
