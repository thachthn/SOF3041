package com.fpt.testingasm.service;

import com.fpt.testingasm.model.Product;
import com.fpt.testingasm.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business rules (CRUD sản phẩm):
 *
 * BR8.  Tên sản phẩm bắt buộc, không rỗng.
 * BR9.  Giá sản phẩm phải >= 0.
 * BR10. Số lượng phải >= 0 (không âm kho).
 * BR11. Xoá sản phẩm không tồn tại -> không lỗi hệ thống (idempotent), trả về false.
 * BR12. Cập nhật sản phẩm không tồn tại -> ném NotFound.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(Long id) {
            super("Không tìm thấy sản phẩm với id = " + id);
        }
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setQuantity(updated.getQuantity());
        return productRepository.save(existing);
    }

    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }
}
