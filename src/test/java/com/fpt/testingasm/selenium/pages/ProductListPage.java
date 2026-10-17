package com.fpt.testingasm.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Page Object cho trang /products */
public class ProductListPage {

    private final WebDriver driver;
    private final String baseUrl;

    private final By addButton = By.id("add-product-btn");
    private final By logoutLink = By.id("logout-link");
    private final By productRows = By.className("product-row");

    public ProductListPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public ProductListPage open() {
        driver.get(baseUrl + "/products");
        return this;
    }

    public ProductFormPage clickAddProduct() {
        // Đã thêm Wait: Chờ nút "Thêm" xuất hiện và có thể click (tối đa 10s)
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(addButton)).click();
        return new ProductFormPage(driver, baseUrl);
    }

    public ProductFormPage clickEdit(long productId) {
        // Đã thêm Wait
        By editBtn = By.cssSelector("tr[data-id='" + productId + "'] .edit-link");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(editBtn)).click();
        return new ProductFormPage(driver, baseUrl);
    }

    public void clickDelete(long productId) {
        // Đã thêm Wait
        By deleteBtn = By.cssSelector("tr[data-id='" + productId + "'] .delete-btn");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();

        // Trang có window.confirm(); Selenium cần chấp nhận alert trước khi tiếp tục
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    public List<WebElement> getProductRows() {
        return driver.findElements(productRows);
    }

    public boolean containsProductNamed(String name) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("product-table")));
        return driver.findElements(By.className("product-name")).stream()
                .anyMatch(el -> el.getText().equals(name));
    }

    /**
     * Tìm id (data-id) của hàng sản phẩm theo tên hiển thị, để thao tác sửa/xoá
     * đúng sản phẩm vừa tạo.
     */
    public long getProductIdByName(String name) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("product-table")));
        return getProductRows().stream()
                .filter(row -> row.findElement(By.className("product-name")).getText().equals(name))
                .findFirst()
                .map(row -> Long.parseLong(row.getAttribute("data-id")))
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy sản phẩm tên: " + name));
    }

    public void logout() {
        // Đã thêm Wait
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }
}