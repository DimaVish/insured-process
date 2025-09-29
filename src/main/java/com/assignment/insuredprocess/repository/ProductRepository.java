package com.assignment.insuredprocess.repository;

import com.assignment.insuredprocess.model.Product;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final Map<String, Product> products = new HashMap<>();

    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    //Preparation for additional functionality
    public List<Product> findAll() {
        return products.values().stream().toList();
    }

    public boolean existsById(String id) {
        return products.containsKey(id);
    }
}