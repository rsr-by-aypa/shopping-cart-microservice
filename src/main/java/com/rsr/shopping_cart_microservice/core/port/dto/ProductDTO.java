package com.rsr.shopping_cart_microservice.core.port.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private double priceInEuro;
    private int numberInStock;
    private String imageLink;
}
