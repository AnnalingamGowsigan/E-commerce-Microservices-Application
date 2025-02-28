package com.springboot.productservice.controller;

import com.springboot.productservice.dto.ProductRequest;
import com.springboot.productservice.dto.ProductResponse;
import com.springboot.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        // Call the service to create a new product
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        // Call the service to get all products
        return productService.getAllProducts();
    }

    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.OK)
    public String getHello() {
        return "Hello from product-service!";
    }
}
