package com.fpt.testingasm.service;

import com.fpt.testingasm.model.Product;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit/Integration test cho ProductService.
 * Tương ứng test case: TC-PRD-01..06, 09, 11 (file 02_TestCases.md)
 */
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    // TC-PRD-01: Tạo sản phẩm hợp lệ
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRD-01: Tạo sản phẩm với dữ liệu hợp lệ phải thành công")
    void create_withValidData_shouldSucceed() {
        Product product = productService.create(new Product("Chuột không dây", "Logitech M185", 150000.0, 10));

        assertNotNull(product.getId());
        assertEquals("Chuột không dây", product.getName());
    }

    // TC-PRD-02: Tên sản phẩm rỗng
    @Test
    @DisplayName("TC-PRD-02: Tên sản phẩm rỗng phải vi phạm validation")
    void create_withBlankName_shouldViolateValidation() {
        Product invalid = new Product("", "desc", 10000.0, 1);
        assertThrows(ConstraintViolationException.class, () -> productService.create(invalid));
    }

    // TC-PRD-03: Giá âm
    @Test
    @DisplayName("TC-PRD-03: Giá âm phải vi phạm validation")
    void create_withNegativePrice_shouldViolateValidation() {
        Product invalid = new Product("Sản phẩm giá âm", "desc", -10.0, 1);
        assertThrows(ConstraintViolationException.class, () -> productService.create(invalid));
    }

    // TC-PRD-04: Giá = 0 (biên hợp lệ)
    @Test
    @DisplayName("TC-PRD-04: Giá = 0 (biên hợp lệ) phải tạo được")
    void create_withZeroPrice_shouldSucceed() {
        Product product = productService.create(new Product("Sản phẩm tặng kèm", "desc", 0.0, 5));
        assertNotNull(product.getId());
    }

    // TC-PRD-05: Số lượng âm
    @Test
    @DisplayName("TC-PRD-05: Số lượng âm phải vi phạm validation")
    void create_withNegativeQuantity_shouldViolateValidation() {
        Product invalid = new Product("Sản phẩm số lượng âm", "desc", 1000.0, -1);
        assertThrows(ConstraintViolationException.class, () -> productService.create(invalid));
    }

    // TC-PRD-06: Số lượng = 0 (biên hợp lệ)
    @Test
    @DisplayName("TC-PRD-06: Số lượng = 0 (biên hợp lệ) phải tạo được")
    void create_withZeroQuantity_shouldSucceed() {
        Product product = productService.create(new Product("Sản phẩm hết hàng", "desc", 1000.0, 0));
        assertNotNull(product.getId());
    }

    // TC-PRD-08: Sửa sản phẩm tồn tại
    @Test
    @DisplayName("TC-PRD-08: Sửa sản phẩm tồn tại phải cập nhật đúng dữ liệu")
    void update_withExistingId_shouldUpdateFields() {
        Product created = productService.create(new Product("Tên cũ", "Mô tả cũ", 100.0, 1));

        Product updated = productService.update(created.getId(),
                new Product("Tên mới", "Mô tả mới", 200.0, 2));

        assertEquals("Tên mới", updated.getName());
        assertEquals(200.0, updated.getPrice());
        assertEquals(2, updated.getQuantity());
    }

    // TC-PRD-09: Sửa sản phẩm không tồn tại
    @Test
    @DisplayName("TC-PRD-09: Sửa sản phẩm không tồn tại phải ném ProductNotFoundException")
    void update_withNonExistentId_shouldThrow() {
        assertThrows(ProductService.ProductNotFoundException.class,
                () -> productService.update(999999L, new Product("x", "y", 1.0, 1)));
    }

    // TC-PRD-10: Xoá sản phẩm tồn tại
    @Test
    @DisplayName("TC-PRD-10: Xoá sản phẩm tồn tại phải trả về true và xoá khỏi DB")
    void delete_withExistingId_shouldReturnTrue() {
        Product created = productService.create(new Product("Sẽ bị xoá", "desc", 50.0, 1));

        boolean result = productService.delete(created.getId());

        assertTrue(result);
        assertTrue(productService.findById(created.getId()).isEmpty());
    }

    // TC-PRD-11: Xoá sản phẩm không tồn tại
    @Test
    @DisplayName("TC-PRD-11: Xoá sản phẩm không tồn tại phải trả về false, không ném lỗi")
    void delete_withNonExistentId_shouldReturnFalse() {
        boolean result = productService.delete(999999L);
        assertFalse(result);
    }

    // TC-PRD-07: Xem danh sách sản phẩm
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRD-07: findAll phải trả về đúng số sản phẩm đã tạo")
    void findAll_shouldReturnAllCreatedProducts() {
        productService.create(new Product("SP1", "d", 10.0, 1));
        productService.create(new Product("SP2", "d", 20.0, 2));

        assertEquals(2, productService.findAll().size());
    }
}
