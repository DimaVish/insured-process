package com.assignment.insuredprocess.controller;

import com.assignment.insuredprocess.dto.ClientAuthRequest;
import com.assignment.insuredprocess.model.Client;
import com.assignment.insuredprocess.model.Product;
import com.assignment.insuredprocess.service.ClientService;
import com.assignment.insuredprocess.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody ClientAuthRequest request) {
        try {
            Client client = clientService.createClient(
                request.getClientId(), 
                request.getContactType(), 
                request.getContactValue()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(client);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //The auth mechanism could be replaced by using Spring Security
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateClient(@RequestBody ClientAuthRequest request) {
        boolean isAuthenticated = clientService.authenticateClient(
            request.getClientId(),
            request.getContactType(),
            request.getContactValue()
        );
        
        if (isAuthenticated) {
            return ResponseEntity.ok("Client authenticated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @GetMapping("/{clientId}/products")
    public ResponseEntity<List<Product>> getClientProducts(@PathVariable String clientId) {
        if (clientService.findClientById(clientId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Product> products = productService.getClientProducts(clientId);
        return ResponseEntity.ok(products);
    }
}