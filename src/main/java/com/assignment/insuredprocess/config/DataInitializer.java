package com.assignment.insuredprocess.config;

import com.assignment.insuredprocess.service.ClientService;
import com.assignment.insuredprocess.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClientService clientService;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        productService.createProduct("P001", "Health Insurance", "Comprehensive health coverage");
        productService.createProduct("P002", "Auto Insurance", "Vehicle protection coverage");
        productService.createProduct("P003", "Life Insurance", "Life protection for your family");

        clientService.createClient("C001", "email", "john@example.com");
        clientService.createClient("C002", "phone", "555-1234");

        log.info("Sample data initialized successfully!");
    }
}