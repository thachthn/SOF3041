package com.fpt.testingasm.selenium;

import com.fpt.testingasm.selenium.pages.LoginPage;
import com.fpt.testingasm.selenium.pages.ProductListPage;
import com.fpt.testingasm.selenium.pages.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium UI test: luồng CRUD sản phẩm trên giao diện web (đăng ký + đăng nhập
 * trước, theo yêu cầu Y4).
 * Tương ứng test case: TC-PRD-01, TC-PRD-07, TC-PRD-08, TC-PRD-10 (file
 * 02_TestCases.md)
 *
 * Nhóm "ui": chạy riêng bằng: mvn test -Dgroups=ui
 */
@Tag("ui")
class ProductCrudSeleniumTest extends SeleniumTestBase {

    private ProductListPage productListPage;

    @BeforeEach
    void registerAndLogin() {
        String username = "cruduser" + System.currentTimeMillis();
        new RegisterPage(driver, baseUrl).open().register(username, username + "@gmail.com", "mypassword");
        productListPage = new LoginPage(driver, baseUrl).open().login(username, "mypassword");
    }

    // TC-PRD-01 + TC-PRD-07 (UI)
    @Test
    @DisplayName("[UI] TC-PRD-01/07: Thêm sản phẩm mới và thấy xuất hiện trong danh sách")
    void addProduct_thenAppearsInList() {
        productListPage
                .clickAddProduct()
                .fillAndSave("Bàn phím cơ Selenium", "Test UI", 990000, 3);

        assertTrue(productListPage.containsProductNamed("Bàn phím cơ Selenium"));
    }

    // TC-PRD-08 (UI)
    @Test
    @DisplayName("[UI] TC-PRD-08: Sửa sản phẩm và thấy tên mới trong danh sách")
    void editProduct_thenListShowsUpdatedName() {
        productListPage
                .clickAddProduct()
                .fillAndSave("Tên trước khi sửa", "Test UI", 100, 1);

        assertTrue(productListPage.containsProductNamed("Tên trước khi sửa"));

        long id = productListPage.getProductIdByName("Tên trước khi sửa");
        productListPage.clickEdit(id)
                .fillAndSave("Tên sau khi sửa", "Test UI", 150, 2);

        assertTrue(productListPage.containsProductNamed("Tên sau khi sửa"));
    }

    // TC-PRD-10 (UI)
    @Test
    @DisplayName("[UI] TC-PRD-10: Xoá sản phẩm và không còn thấy trong danh sách")
    void deleteProduct_thenNotInList() {
        productListPage
                .clickAddProduct()
                .fillAndSave("Sản phẩm sẽ bị xoá", "Test UI", 100, 1);

        assertTrue(productListPage.containsProductNamed("Sản phẩm sẽ bị xoá"));

        long id = productListPage.getProductIdByName("Sản phẩm sẽ bị xoá");
        productListPage.open();
        productListPage.clickDelete(id);

        productListPage.open();
        assertTrue(!productListPage.containsProductNamed("Sản phẩm sẽ bị xoá"));
    }
}
