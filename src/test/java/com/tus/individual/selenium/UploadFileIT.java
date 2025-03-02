package com.tus.individual.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.regex.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UploadFileIT {
    private WebDriver driver;
    private WebDriverWait wait;
    private String jwtToken; // Store JWT for session persistence
    
    @TempDir
    private Path downloadDir;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        
        // Create ChromeOptions and set download preferences
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", downloadDir.toAbsolutePath().toString());
		chromePrefs.put("savefile.default_directory", downloadDir.toAbsolutePath().toString());

        ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8081"); // Load SPA

        // ✅ Perform Login before testing upload
        loginSuccessfully();
        
    }

    @Test
    void testFileUploadAndDownload() throws IOException, InterruptedException {
        System.out.println("➡ Navigating to File Upload page...");

        restoreSessionWithJWT(); // ✅ Ensure session is restored before clicking

        // ✅ Click the "Upload File" navigation link
        WebElement uploadNav = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href=\"#file-upload\"]")));
        uploadNav.click();

        // ✅ Wait for file dropdown to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileDropdown")));
        System.out.println("✅ File Upload Page loaded!");

        // ✅ Select the first file in the dropdown
        WebElement fileDropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileDropdown")));
        Select fileDropdown = new Select(fileDropdownElement);
        
        // Wait until the specific value "Dataset 3A.xls" is present in the dropdown options
        wait.until(ExpectedConditions.textToBePresentInElement(fileDropdownElement, "Dataset 3A.xls"));
        fileDropdown.selectByValue("Dataset 3A.xls");
        System.out.println("✅ Selected first file from dropdown.");

        // ✅ Click the upload button
        WebElement uploadButton = driver.findElement(By.id("uploadButton"));
        uploadButton.click();
        System.out.println("➡ Upload started... Waiting for completion...");

        // ✅ Wait for extended time (increase wait for large uploads)
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(2*60));
        WebElement successMessage = longWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#uploadStatus .alert-success")));
        String successText = successMessage.getText();
        System.out.println("✅ Success Message: " + successText);

        // ✅ Ensure the success message contains "Upload Complete"
        assertTrue(successText.contains("Upload Complete"), "❌ Expected 'Upload Complete' in success message.");

        // ✅ Extract "Skipped Rows" count from the success message using regex
        int expectedSkippedRows = extractSkippedRowCount(successText);
        System.out.println("✅ Expected Skipped Rows: " + expectedSkippedRows);

        // ✅ Ensure downloadSkippedLog button is visible
        WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("downloadSkippedLog")));
        assertTrue(downloadButton.isDisplayed(), "❌ Skipped log download button is not visible after upload.");

        // ✅ Click the download button
        downloadButton.click();
        System.out.println("➡ Downloading skipped log file...");

        // ✅ Wait for file download (poll every 2 seconds, up to 20 seconds)
        File downloadedFile = waitForDownloadedFile("skipped_rows.log", 20);
        assertTrue(downloadedFile.exists(), "❌ Downloaded file was not found.");

        // ✅ Count the number of lines in the downloaded file
        int actualSkippedRows = countLinesInFile(downloadedFile);
        System.out.println("✅ Skipped Rows in Log File: " + actualSkippedRows);

        // ✅ Ensure the number of skipped rows in the file matches the alert
        assertEquals(expectedSkippedRows, actualSkippedRows, "❌ Skipped rows count does not match!");

        System.out.println("✅ File upload and validation test passed!");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit(); // Close browser
        }
    }
    
    
    // Helper Functions
    // ✅ Extracts "Skipped Rows" count from success message
    private int extractSkippedRowCount(String successText) {
        Pattern pattern = Pattern.compile("(?i)(\\d+) skipped");
        Matcher matcher = pattern.matcher(successText);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    // ✅ Waits for the file to appear in the download folder
    private File waitForDownloadedFile(String fileName, int maxWaitSeconds) throws InterruptedException {
        File file = downloadDir.resolve(fileName).toFile();
        System.out.println("Temp directory: " + downloadDir);

        int waited = 0;
        while (waited < maxWaitSeconds) {
            if (file.exists()) {
                return file;
            }
            Thread.sleep(2000); // Wait 2 seconds before checking again
            waited += 2;
        }
        return file; // Return file (may or may not exist)
    }

    // ✅ Counts lines in a given file
    private int countLinesInFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return (int) reader.lines().count();
        }
    }
    
    private void loginSuccessfully() {
        System.out.println("➡ Logging in as Admin for file upload...");

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
        assertTrue(driver.findElement(By.id("logoutButton")).isDisplayed(), "❌ Login failed, cannot proceed to upload.");

        // ✅ Retrieve JWT Token from LocalStorage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        jwtToken = (String) js.executeScript("return localStorage.getItem('jwt');");

        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalStateException("❌ JWT token not found after login.");
        }
        System.out.println("✅ JWT Token retrieved: " + jwtToken);
    }

    private void restoreSessionWithJWT() {
        if (jwtToken != null && !jwtToken.isEmpty()) {
            System.out.println("➡ Restoring session with JWT...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("localStorage.setItem('jwt', arguments[0]);", jwtToken);
            driver.navigate().refresh(); // Reload page with session restored
        }
    }
}
