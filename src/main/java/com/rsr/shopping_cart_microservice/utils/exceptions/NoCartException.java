package com.rsr.shopping_cart_microservice.utils.exceptions;

import java.util.UUID;

public class NoCartException extends NullPointerException {
    public NoCartException(UUID userId) {
        super("There is no Cart for the User: " + userId);
    }
}
