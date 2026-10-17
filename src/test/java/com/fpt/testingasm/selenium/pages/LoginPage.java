package com.fpt.testingasm.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page Object cho trang /login */
public class LoginPage {

    private final WebDriver driver;
    private final String baseUrl;

    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.id("login-submit");
    private final By errorMessage = By.id("error-msg");
    private final By successMessage = By.id("success-msg");

    public LoginPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public LoginPage open() {
        driver.get(baseUrl + "/login");
        return this;
    }

    public ProductListPage login(String username, String password) {
        WebElement username_ = driver.findElement(usernameInput);
        username_.clear();
        username_.sendKeys(username);

        WebElement password_ = driver.findElement(passwordInput);
        password_.clear();
        password_.sendKeys(password);

        driver.findElement(submitButton).click();
        return new ProductListPage(driver, baseUrl);
    }

    /** Dùng khi expect đăng nhập thất bại và ở lại trang login */
    public LoginPage loginExpectingFailure(String username, String password) {
        WebElement username_ = driver.findElement(usernameInput);
        username_.clear();
        username_.sendKeys(username);

        WebElement password_ = driver.findElement(passwordInput);
        password_.clear();
        password_.sendKeys(password);

        driver.findElement(submitButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(errorMessage));
        return this;
    }

    public boolean hasError() {
        return !driver.findElements(errorMessage).isEmpty();
    }

    public boolean hasSuccess() {
        return !driver.findElements(successMessage).isEmpty();
    }
}
