package com.ecommerce.productservice.service;

import org.springframework.cache.annotation.Cacheable;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.productservice.dto.ProductAttributeDTO;
import com.ecommerce.productservice.dto.ProductDTO;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.model.ProductAttribute;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable("products")
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        log.info("Fetching all products from database.");
        return productRepository.findAll().stream()
                .map(x->{
                    log.info("Product found: {}", x);
                    return convertToDto(x);
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "product", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Fetching product by ID {} from database.", id);
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.map(this::convertToDto).orElse(null); // Or throw a custom exception
    }

    // Helper method to convert Product entity to ProductDTO
    private ProductDTO convertToDto(Product product) {
        String imageUrls = product.getImageUrls();
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : null;
        List<ProductAttributeDTO> attributeDTOs = product.getAttributes().stream()
                .map(attr -> new ProductAttributeDTO(attr.getAttributeName(), attr.getAttributeValue()))
                .collect(Collectors.toList());

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                imageUrls,
                product.getDisplayStatus(),
                categoryName,
                attributeDTOs
        );
    }

    // This method will be used by Kafka listener to update display status
    @Transactional
    public void updateProductDisplayStatus(Long productId, int newQuantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        productOptional.ifPresent(product -> {
            if (newQuantity <= 0) {
                product.setDisplayStatus("Hết hàng");
            } else {
                product.setDisplayStatus("Còn hàng");
            }
            productRepository.save(product);
        });
        // Optionally, handle product not found for a given productId
    }
}