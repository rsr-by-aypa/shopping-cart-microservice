package com.rsr.shopping_cart_microservice.core.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    private UUID id;

    private String name;

    private double priceInEuro;

    private int numberInStock;

    private String imageLink;

    public Product(UUID id, String name, double priceInEuro, int numberInStock, String imageLink) {
        this.id = id;
        this.name = name;
        this.priceInEuro = priceInEuro;
        this.numberInStock = numberInStock;
        this.imageLink = imageLink;
    }

}
