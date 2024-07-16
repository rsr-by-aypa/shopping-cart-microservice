package com.rsr.shopping_cart_microservice.core.domain.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Item {

    private UUID productId;

    private String productName;

    private double priceInEuro;

    private int amount;

    private String imageLink;

    public Item(UUID productId, String productName, double priceInEuro, int amount, String imageLink) {
        this.productId = productId;
        this.productName = productName;
        this.priceInEuro = priceInEuro;
        this.amount = amount;
        this.imageLink = imageLink;
    }

}

