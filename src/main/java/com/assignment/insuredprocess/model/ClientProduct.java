package com.assignment.insuredprocess.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ClientProduct {
    private String clientId;
    private String productId;
    private LocalDateTime purchaseDate;

    public ClientProduct(String clientId, String productId) {
        this.clientId = clientId;
        this.productId = productId;
        this.purchaseDate = LocalDateTime.now();
    }
}