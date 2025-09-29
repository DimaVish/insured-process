package com.assignment.insuredprocess.service;

import com.assignment.insuredprocess.model.ClientProduct;
import com.assignment.insuredprocess.model.Product;
import com.assignment.insuredprocess.repository.ClientProductRepository;
import com.assignment.insuredprocess.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ClientProductRepository clientProductRepository;
    private final ClientService clientService;

    public List<Product> getClientProducts(String clientId) {
        List<ClientProduct> clientProducts = clientProductRepository.findByClientId(clientId);
        return clientProducts.stream()
                .map(cp -> productRepository.findById(cp.getProductId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Product buyProduct(String clientId, String productId) {
        if (clientService.findClientById(clientId).isEmpty()) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        if (clientProductRepository.existsByClientIdAndProductId(clientId, productId)) {
            throw new IllegalArgumentException("Client already owns this product");
        }

        ClientProduct clientProduct = new ClientProduct(clientId, productId);
        clientProductRepository.save(clientProduct);
        return productOpt.get();
    }

    public void updateProduct(String clientId, String productId, String newName, String newDescription) {
        if (!clientProductRepository.existsByClientIdAndProductId(clientId, productId)) {
            throw new IllegalArgumentException("Client does not own this product");
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (newName != null) product.setName(newName);
            if (newDescription != null) product.setDescription(newDescription);
            productRepository.save(product);
        }
    }

    public Product createProduct(String id, String name, String description) {
        if (productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product with ID " + id + " already exists");
        }
        Product product = new Product(id, name, description);
        return productRepository.save(product);
    }
}