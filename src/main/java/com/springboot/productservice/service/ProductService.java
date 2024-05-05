package com.springboot.productservice.service;

import com.springboot.productservice.dto.ProductRequest;
import com.springboot.productservice.dto.ProductResponse;
import com.springboot.productservice.model.Product;
import com.springboot.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required arguments in compile time
@Slf4j // Lombok will generate a logger field
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        // Create a new product
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);

        log.info("Product created successfully. Product: {}", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        // Get all products
       List<Product> products = productRepository.findAll();

       log.info("Retrieved all products. Total products: {}", products.size());

       return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .build())
                .toList();
    }
}
