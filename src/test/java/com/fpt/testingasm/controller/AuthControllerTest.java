package com.fpt.testingasm.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test (MockMvc) cho AuthController.
 * Tương ứng test case: TC-REG-01, TC-LOG-01, TC-LOG-02, TC-LOG-05 (file 02_TestCases.md)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // TC-REG-01
    @Test
    @Tag("smoke")
    @DisplayName("TC-REG-01: POST /register với dữ liệu hợp lệ trả về view login kèm thông báo thành công")
    void postRegister_withValidData_returnsLoginViewWithSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "controlleruser1")
                        .param("email", "controlleruser1@gmail.com")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("success"));
    }

    @Test
    @DisplayName("Đăng ký trùng username phải trả lại view register kèm lỗi")
    void postRegister_withDuplicateUsername_returnsRegisterViewWithError() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "dupctrluser")
                .param("email", "dup1@gmail.com")
                .param("password", "123456"));

        mockMvc.perform(post("/register")
                        .param("username", "dupctrluser")
                        .param("email", "dup2@gmail.com")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    // TC-LOG-01
    @Test
    @Tag("smoke")
    @DisplayName("TC-LOG-01: Đăng nhập đúng tài khoản phải redirect /products")
    void postLogin_withCorrectCredentials_redirectsToProducts() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "logctrl1")
                .param("email", "logctrl1@gmail.com")
                .param("password", "mypassword"));

        mockMvc.perform(post("/login")
                        .param("username", "logctrl1")
                        .param("password", "mypassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    // TC-LOG-02
    @Test
    @Tag("smoke")
    @DisplayName("TC-LOG-02: Đăng nhập sai password phải trả lại view login kèm lỗi")
    void postLogin_withWrongPassword_returnsLoginViewWithError() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "logctrl2")
                .param("email", "logctrl2@gmail.com")
                .param("password", "correctpass"));

        mockMvc.perform(post("/login")
                        .param("username", "logctrl2")
                        .param("password", "wrongpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    // TC-LOG-05
    @Test
    @DisplayName("TC-LOG-05: Truy cập /products khi chưa đăng nhập phải redirect /login")
    void getProducts_withoutLogin_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
