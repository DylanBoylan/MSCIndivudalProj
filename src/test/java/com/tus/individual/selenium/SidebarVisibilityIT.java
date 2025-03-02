package com.tus.individual.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SidebarVisibilityIT {
    private WebDriver driver;
    private WebDriverWait wait;
    private String jwtToken; // Store JWT for session persistence

    // Expected sidebar items for each role
    private final Map<String, List<String>> expectedNavs = Map.of(
        "ADMINISTRATOR", List.of(
        		"homeNav", 
        		"fileUploadNav", 
        		"registerNav", 
        		"accountsNav", 
        		"logoutButton"),
        "NETWORK_MANAGEMENT_ENGINEER", List.of(
        		"homeNav", 
        		"byimsisNav", 
        		"imsisNav", 
        		"failuresNav", 
        		"countByImsiNav", 
        		"summaryNav", 
        		"byimsitimeNav",
        		"logoutButton",
        		"top10operatorsNav"),
        "SUPPORT_ENGINEER", List.of(
        		"homeNav", 
        		"byimsisNav", 
        		"imsisNav", 
        		"failuresNav", 
        		"byimsitimeNav", 
        		"logoutButton"),
        "CUSTOMER_SERVICE", List.of(
        		"homeNav", 
        		"byimsisNav", 
        		"byimsitimeNav", 
        		"logoutButton")
    );

    private final Map<String, String> testUsers = Map.of(
        "ADMINISTRATOR", "test_administrator@example.com",
        "SUPPORT_ENGINEER", "test_support_engineer@example.com",
        "CUSTOMER_SERVICE", "test_customer_service@example.com",
        "NETWORK_MANAGEMENT_ENGINEER", "test_network_management_engineer@example.com"
    );

    private final String password = "SecurePass123!";

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void logout() {
        System.out.println("➡ Logging out...");

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
        logoutButton.click();

        // ✅ Handle logout alert
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException ignored) {}

        // ✅ Wait for sidebar to fully disappear before continuing
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("sidebar")));

        System.out.println("✅ Logged out, clearing JWT...");
        jwtToken = null; // Clear stored token
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testAdminSidebarVisibility() {
        login("ADMINISTRATOR");
        checkSidebarVisibility("ADMINISTRATOR");
    }

    @Test
    void testSupportEngineerSidebarVisibility() {
        login("SUPPORT_ENGINEER");
        checkSidebarVisibility("SUPPORT_ENGINEER");
    }

    @Test
    void testCustomerServiceSidebarVisibility() {
        login("CUSTOMER_SERVICE");
        checkSidebarVisibility("CUSTOMER_SERVICE");
    }

    @Test
    void testNetworkManagementEngineerSidebarVisibility() {
        login("NETWORK_MANAGEMENT_ENGINEER");
        checkSidebarVisibility("NETWORK_MANAGEMENT_ENGINEER");
    }
    
    
    // Helper functions
    void login(String role) {
        System.out.println("➡ Logging in as: " + role);
        driver.get("http://localhost:8081");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
        WebElement passwordField = driver.findElement(By.id("loginPassword"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        emailField.sendKeys(testUsers.get(role));
        passwordField.sendKeys(password);
        loginButton.click();

        // Handle login alert
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException ignored) {}

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton"))); // Ensure logged in
        System.out.println("✅ Successfully logged in as " + role);

        // ✅ Retrieve JWT Token from LocalStorage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        jwtToken = (String) js.executeScript("return localStorage.getItem('jwt');");

        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalStateException("❌ JWT token not found after login.");
        }
        System.out.println("✅ JWT Token retrieved: " + jwtToken);
    }

    void restoreSessionWithJWT() {
        if (jwtToken != null && !jwtToken.isEmpty()) {
            System.out.println("➡ Restoring session with JWT...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("localStorage.setItem('jwt', arguments[0]);", jwtToken);
            driver.navigate().refresh(); // Reload page with session restored
        }
    }

    void checkSidebarVisibility(String role) {
        List<String> expectedItems = new ArrayList<>(expectedNavs.get(role)); // Ensure it's modifiable

        System.out.println("✅ Checking sidebar for role: " + role);

        restoreSessionWithJWT(); // ✅ Restore session before checking sidebar

        // ✅ Wait for sidebar to become visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sidebar")));

        // ✅ Ensure all expected items are visible before verification
        for (String itemId : expectedItems) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(itemId)));
                System.out.println("✅ Found sidebar item: " + itemId);
            } catch (TimeoutException e) {
                System.out.println("⚠ Warning: Sidebar item not found -> " + itemId);
            }
        }

        // ✅ Capture visible sidebar items
        List<WebElement> visibleNavItems = driver.findElements(By.cssSelector("#sidebar .nav-item:not([style*='display: none'])"));
        List<String> visibleIds = new ArrayList<>(visibleNavItems.stream()
            .map(item -> item.getAttribute("id"))
            .filter(id -> id != null && !id.isEmpty()) // Ignore elements without an ID
            .toList());

        // ✅ Ensure logout button is included
        WebElement logoutButton = driver.findElement(By.id("logoutButton"));
        boolean isLogoutVisible = logoutButton.isDisplayed();

        if (!visibleIds.contains("logoutButton") && isLogoutVisible) {
            visibleIds.add("logoutButton");
        }

        System.out.println("✅ Detected Sidebar Items: " + visibleIds);

        // ✅ Validate expected vs detected
        assertTrue(new HashSet<>(visibleIds).equals(new HashSet<>(expectedItems)),
                "❌ Sidebar mismatch for " + role + ". Expected: " + expectedItems + ", Found: " + visibleIds);
    }
}
