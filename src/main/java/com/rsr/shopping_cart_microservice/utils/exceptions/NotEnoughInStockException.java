package com.rsr.shopping_cart_microservice.utils.exceptions;

import java.util.UUID;

public class NotEnoughInStockException extends IllegalArgumentException{
    public NotEnoughInStockException(UUID uuid) {
        super("There are not enough items in stock.");
    }
}
