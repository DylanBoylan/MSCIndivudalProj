package com.tus.individual.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Keep WebDriver open across tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RegisterIT {
    private WebDriver driver;
    private WebDriverWait wait;
    private String jwtToken; // Store JWT for session persistence

    @BeforeAll
    void setup() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8081"); // Load SPA

        // ✅ Perform Login and Store JWT
        loginSuccessfully();
    }

    @Test
    @DisplayName("✅ Register All User Roles")
    void testAdminRegistersAllRoles() {
        List<String> roles = List.of(
            "CUSTOMER_SERVICE",
            "SUPPORT_ENGINEER",
            "ADMINISTRATOR",
            "NETWORK_MANAGEMENT_ENGINEER"
        );

        for (String role : roles) {
            registerUser(role);
        }
    }

    @Test
    void testRegisterExistingEmail() {
        System.out.println("➡ Testing registration with existing email...");

        navigateToRegisterPage();

        WebElement emailField = driver.findElement(By.id("registerEmail"));
        WebElement passwordField = driver.findElement(By.id("registerPassword"));
        WebElement roleDropdown = driver.findElement(By.id("registerRole"));
        WebElement registerButton = driver.findElement(By.id("registerButton"));

        emailField.sendKeys("test_customer_service@example.com"); // Already registered
        passwordField.sendKeys("SecurePass123!");
        new Select(roleDropdown).selectByValue("CUSTOMER_SERVICE");

        registerButton.click();

        WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#registerStatus .alert-danger")));
        assertTrue(errorAlert.getText().contains("Email already exists"), "❌ Expected 'Email already exists' error.");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit(); // Close browser
        }
    }
    
    
    // Helper methods
    void registerUser(String role) {
        System.out.println("➡ Registering user for role: " + role);

        navigateToRegisterPage();

        WebElement emailField = driver.findElement(By.id("registerEmail"));
        WebElement passwordField = driver.findElement(By.id("registerPassword"));
        WebElement roleDropdown = driver.findElement(By.id("registerRole"));
        WebElement registerButton = driver.findElement(By.id("registerButton"));

        // ✅ Fill in registration details
        String testEmail = "registration_test_" + role.toLowerCase() + "@example.com";
        emailField.sendKeys(testEmail);
        passwordField.sendKeys("SecurePass123!");
        new Select(roleDropdown).selectByValue(role);

        registerButton.click();
        System.out.println("➡ Register button clicked, checking for success message...");

        // ✅ Wait for success message in #registerStatus
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#registerStatus .alert-success")));
        String successText = successMessage.getText();
        System.out.println("✅ Success Message: " + successText);

        assertTrue(successText.contains(testEmail) && successText.contains(role),
                "❌ Expected success message to confirm registration.");

        System.out.println("✅ Registration successful for role: " + role);
    }
    
    
    void loginSuccessfully() {
        System.out.println("➡ Logging in as Admin for registration...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement emailField = driver.findElement(By.id("loginEmail"));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys("admin@networksys.com");
        passwordField.sendKeys("Admin123!");
        loginButton.click();

        // ✅ Handle alert if present
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException ignored) {}

        // ✅ Verify login by checking logout button visibility
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
        assertTrue(driver.findElement(By.id("logoutButton")).isDisplayed(), "❌ Login failed, cannot proceed to register.");

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
            driver.navigate().refresh(); // Reload the page with session restored
        }
    }

    void navigateToRegisterPage() {
        System.out.println("➡ Navigating to Register Page...");

        restoreSessionWithJWT(); // ✅ Ensure session is restored

        // ✅ Wait for the sidebar to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sidebar")));

        // ✅ Wait for the Register link to be interactable
        WebElement registerNav = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='#register']")));

        // ✅ Scroll to make sure the element is in view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", registerNav);

        // ✅ Click using JavaScript if normal click doesn't work
        try {
            registerNav.click();
        } catch (Exception e) {
            System.out.println("⚠️ Normal click failed, using JavaScript click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerNav);
        }

        // ✅ Ensure navigation actually happens
        wait.until(ExpectedConditions.urlContains("register")); // Change this based on actual URL structure
        System.out.println("📍 New URL after clicking: " + driver.getCurrentUrl());

        // ✅ Wait for Register page elements to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registerEmail")));

        System.out.println("✅ Successfully navigated to the Register page.");
    }
}
