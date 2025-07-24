package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    // Custom query methods can be added here if needed
}