package com.fpt.testingasm.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Page Object cho trang /register */
public class RegisterPage {

    private final WebDriver driver;
    private final String baseUrl;

    private final By usernameInput = By.id("username");
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.id("register-submit");
    private final By errorMessage = By.id("error-msg");

    public RegisterPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public RegisterPage open() {
        driver.get(baseUrl + "/register");
        return this;
    }

    public RegisterPage register(String username, String email, String password) {
        WebElement username_ = driver.findElement(usernameInput);
        username_.clear();
        username_.sendKeys(username);

        WebElement email_ = driver.findElement(emailInput);
        email_.clear();
        email_.sendKeys(email);

        WebElement password_ = driver.findElement(passwordInput);
        password_.clear();
        password_.sendKeys(password);

        driver.findElement(submitButton).click();
        return this;
    }

    public boolean hasError() {
        return !driver.findElements(errorMessage).isEmpty();
    }

    public String getErrorText() {
        return driver.findElement(errorMessage).getText();
    }
}
