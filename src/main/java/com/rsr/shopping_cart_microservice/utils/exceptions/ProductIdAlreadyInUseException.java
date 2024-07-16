package com.rsr.shopping_cart_microservice.utils.exceptions;

import java.util.UUID;

public class ProductIdAlreadyInUseException extends IllegalArgumentException {
    public ProductIdAlreadyInUseException(UUID uuid) {
        super("Product Id already exists: " + uuid);
    }
}
