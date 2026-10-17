package com.fpt.testingasm.selenium;

import com.fpt.testingasm.selenium.pages.LoginPage;
import com.fpt.testingasm.selenium.pages.ProductListPage;
import com.fpt.testingasm.selenium.pages.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI test: luồng đăng ký -> đăng nhập -> đăng xuất.
 * Tương ứng test case: TC-REG-01, TC-LOG-01, TC-LOG-02, TC-LOG-06 (file 02_TestCases.md)
 *
 * Nhóm "ui": không chạy trong lần build mặc định (xem pom.xml, excludedGroups=ui),
 * chạy riêng bằng: mvn test -Dgroups=ui
 */
@Tag("ui")
class RegisterLoginSeleniumTest extends SeleniumTestBase {

    // TC-REG-01 (UI)
    @Test
    @DisplayName("[UI] TC-REG-01: Đăng ký thành công hiển thị thông báo trên trang login")
    void register_withValidData_showsSuccessOnLoginPage() {
        String username = "seleniumuser" + System.currentTimeMillis();

        new RegisterPage(driver, baseUrl)
                .open()
                .register(username, username + "@gmail.com", "123456");

        LoginPage loginPage = new LoginPage(driver, baseUrl);
        assertTrue(loginPage.hasSuccess(), "Phải hiển thị thông báo đăng ký thành công");
    }

    // TC-LOG-01 (UI)
    @Test
    @DisplayName("[UI] TC-LOG-01: Đăng nhập đúng tài khoản chuyển tới trang danh sách sản phẩm")
    void login_withCorrectCredentials_navigatesToProductList() {
        String username = "seleniumlogin" + System.currentTimeMillis();
        new RegisterPage(driver, baseUrl).open().register(username, username + "@gmail.com", "mypassword");

        ProductListPage productListPage = new LoginPage(driver, baseUrl)
                .open()
                .login(username, "mypassword");

        assertTrue(driver.getCurrentUrl().endsWith("/products"));
    }

    // TC-LOG-02 (UI)
    @Test
    @DisplayName("[UI] TC-LOG-02: Đăng nhập sai mật khẩu hiển thị lỗi, ở lại trang login")
    void login_withWrongPassword_showsErrorAndStaysOnLoginPage() {
        String username = "seleniumwrongpw" + System.currentTimeMillis();
        new RegisterPage(driver, baseUrl).open().register(username, username + "@gmail.com", "correctpass");

        LoginPage loginPage = new LoginPage(driver, baseUrl)
                .open()
                .loginExpectingFailure(username, "wrongpass");

        assertTrue(loginPage.hasError());
        assertTrue(driver.getCurrentUrl().endsWith("/login"));
    }

    // TC-LOG-06 (UI)
    @Test
    @DisplayName("[UI] TC-LOG-06: Đăng xuất rồi truy cập lại /products bị đưa về trang login")
    void logout_thenAccessProducts_redirectsToLogin() {
        String username = "seleniumlogout" + System.currentTimeMillis();
        new RegisterPage(driver, baseUrl).open().register(username, username + "@gmail.com", "mypassword");

        ProductListPage productListPage = new LoginPage(driver, baseUrl)
                .open()
                .login(username, "mypassword");

        productListPage.logout();
        assertTrue(driver.getCurrentUrl().endsWith("/login"));

        driver.get(baseUrl + "/products");
        assertTrue(driver.getCurrentUrl().endsWith("/login"),
                "Truy cập /products sau khi đăng xuất phải bị redirect về /login");
    }
}
