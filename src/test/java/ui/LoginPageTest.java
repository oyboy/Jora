package ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPageTest {
    private WebDriver driver;
    private static final String URL = "http://localhost:8081/login";

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--headless", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @Test
    public void correctLogin() {
        loginAs("test@mail.com", "test");
        assertRedirect("http://localhost:8081/home");
    }
    @Test
    public void uncorrectLogin() {
        loginAs("test@mail.com", "uncorrect-password");
        assertRedirect("http://localhost:8081/login?error");
    }

    private void loginAs(String email, String password) {
        driver.get(URL);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button.btn.btn-primary.btn-block")).click();
    }

    private void assertRedirect(String expectedUrl) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlToBe(expectedUrl));
        Assertions.assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}