package com.rsr.shopping_cart_microservice.core.port.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CartItemDTO {
    private UUID productId;
    private int amount;
}
