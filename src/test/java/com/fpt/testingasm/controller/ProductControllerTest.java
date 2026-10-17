package com.fpt.testingasm.controller;

import com.fpt.testingasm.model.Product;
import com.fpt.testingasm.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test (MockMvc) cho ProductController.
 * Tương ứng test case: TC-PRD-01, 07, 08, 10, 12 (file 02_TestCases.md)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    /** Giả lập một session đã đăng nhập (bỏ qua bước gọi API đăng nhập thật để test tập trung vào CRUD). */
    private MockHttpSession loggedInSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", "testUserForProductIT");
        return session;
    }

    // TC-PRD-01
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRD-01: Tạo sản phẩm hợp lệ phải redirect /products")
    void postCreateProduct_withValidData_redirectsToList() throws Exception {
        mockMvc.perform(post("/products")
                        .session(loggedInSession())
                        .param("name", "Bàn phím cơ")
                        .param("description", "Keychron K2")
                        .param("price", "990000")
                        .param("quantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    // TC-PRD-07
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRD-07: GET /products phải trả về view list kèm danh sách sản phẩm")
    void getProducts_whenLoggedIn_returnsListView() throws Exception {
        mockMvc.perform(get("/products").session(loggedInSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));
    }

    // TC-PRD-08
    @Test
    @DisplayName("TC-PRD-08: Sửa sản phẩm tồn tại phải cập nhật và redirect /products")
    void postUpdateProduct_withExistingId_updatesAndRedirects() throws Exception {
        MockHttpSession session = loggedInSession();

        Long id = productService.create(new Product("Tên cũ", "d", 100.0, 1)).getId();

        mockMvc.perform(get("/products/" + id + "/edit").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"));

        mockMvc.perform(post("/products/" + id)
                        .session(session)
                        .param("name", "Tên mới")
                        .param("description", "d2")
                        .param("price", "200")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        org.junit.jupiter.api.Assertions.assertEquals("Tên mới",
                productService.findById(id).orElseThrow().getName());
    }

    // TC-PRD-10
    @Test
    @DisplayName("TC-PRD-10: Xoá sản phẩm tồn tại phải redirect /products")
    void postDeleteProduct_withExistingId_redirectsToList() throws Exception {
        MockHttpSession session = loggedInSession();

        Long id = productService.create(new Product("Sẽ bị xoá", "d", 50.0, 1)).getId();

        mockMvc.perform(post("/products/" + id + "/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        org.junit.jupiter.api.Assertions.assertTrue(productService.findById(id).isEmpty());
    }

    // TC-PRD-12
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRD-12: Truy cập /products/new khi chưa đăng nhập phải redirect /login")
    void getNewProductForm_withoutLogin_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
