package com.rsr.shopping_cart_microservice.core.port.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ShoppingCartDTO {
    private UUID userId;
    private List<CartItemDTO> items;
}
