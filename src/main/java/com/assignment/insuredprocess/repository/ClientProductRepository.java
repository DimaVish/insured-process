package com.assignment.insuredprocess.repository;

import com.assignment.insuredprocess.model.ClientProduct;
import com.assignment.insuredprocess.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClientProductRepository {
    private final List<ClientProduct> clientProducts = new ArrayList<>();

    public ClientProduct save(ClientProduct clientProduct) {
        clientProducts.add(clientProduct);
        return clientProduct;
    }

    public List<ClientProduct> findByClientId(String clientId) {
        return clientProducts.stream()
                .filter(clientProduct -> clientProduct.getClientId().equals(clientId))
                .collect(Collectors.toList());
    }

    public boolean existsByClientIdAndProductId(String clientId, String productId) {
        return clientProducts.stream()
                .anyMatch(clientProduct -> clientProduct.getClientId().equals(clientId) && clientProduct.getProductId().equals(productId));
    }

    //Preparation for additional functionality
    public void deleteByClientIdAndProductId(String clientId, String productId) {
        clientProducts.removeIf(clientProduct -> clientProduct.getClientId().equals(clientId) && clientProduct.getProductId().equals(productId));
    }
}