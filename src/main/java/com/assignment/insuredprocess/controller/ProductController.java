package com.assignment.insuredprocess.controller;

import com.assignment.insuredprocess.dto.ProductUpdateRequest;
import com.assignment.insuredprocess.model.Product;
import com.assignment.insuredprocess.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(
                product.getId(),
                product.getName(),
                product.getDescription()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{productId}/buy")
    public ResponseEntity<Product> buyProduct(
            @PathVariable String productId,
            @RequestParam String clientId) {
        try {
            Product product = productService.buyProduct(clientId, productId);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable String productId,
            @RequestParam String clientId,
            @RequestBody ProductUpdateRequest request) {
        try {
            productService.updateProduct(clientId, productId, request.getName(), request.getDescription());
            return ResponseEntity.ok("Product updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}