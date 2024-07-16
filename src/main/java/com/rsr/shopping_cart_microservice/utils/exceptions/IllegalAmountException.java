package com.rsr.shopping_cart_microservice.utils.exceptions;

public class IllegalAmountException extends IllegalArgumentException{
    public IllegalAmountException(int amount) {
        super("There are not " + amount + " items of this product in the Shopping Cart.");
    }
}
