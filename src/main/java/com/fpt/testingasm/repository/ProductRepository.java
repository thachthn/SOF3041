package com.fpt.testingasm.repository;

import com.fpt.testingasm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
