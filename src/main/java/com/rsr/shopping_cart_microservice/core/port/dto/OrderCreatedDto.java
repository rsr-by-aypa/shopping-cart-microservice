package com.rsr.shopping_cart_microservice.core.port.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderCreatedDto {

    private UUID orderId;

    private UUID userId;
}
