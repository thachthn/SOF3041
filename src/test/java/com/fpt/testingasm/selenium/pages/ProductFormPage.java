package com.fpt.testingasm.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Page Object cho form thêm/sửa sản phẩm (/products/new, /products/{id}/edit) */
public class ProductFormPage {

    private final WebDriver driver;
    private final String baseUrl;

    private final By nameInput = By.id("name");
    private final By descriptionInput = By.id("description");
    private final By priceInput = By.id("price");
    private final By quantityInput = By.id("quantity");
    private final By saveButton = By.id("save-product-btn");

    public ProductFormPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public ProductListPage fillAndSave(String name, String description, double price, int quantity) {
        fillField(nameInput, name);
        fillField(descriptionInput, description);
        fillField(priceInput, String.valueOf(price));
        fillField(quantityInput, String.valueOf(quantity));
        driver.findElement(saveButton).click();
        return new ProductListPage(driver, baseUrl);
    }

    private void fillField(By locator, String value) {
        WebElement el = driver.findElement(locator);
        el.clear();
        el.sendKeys(value);
    }
}
