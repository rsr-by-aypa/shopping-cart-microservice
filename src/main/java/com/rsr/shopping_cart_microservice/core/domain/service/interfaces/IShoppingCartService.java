package com.rsr.shopping_cart_microservice.core.domain.service.interfaces;

import com.rsr.shopping_cart_microservice.core.domain.model.ShoppingCart;
import com.rsr.shopping_cart_microservice.utils.exceptions.IllegalAmountException;
import com.rsr.shopping_cart_microservice.utils.exceptions.NoCartException;
import com.rsr.shopping_cart_microservice.utils.exceptions.NotEnoughInStockException;
import com.rsr.shopping_cart_microservice.utils.exceptions.UnknownProductIdException;

import java.util.UUID;

public interface IShoppingCartService {

    void addItemToCart(UUID userId, UUID productId, int amount)
            throws NotEnoughInStockException, UnknownProductIdException;

    void removeItemFromCart(UUID userId, UUID productId, int amount)
            throws UnknownProductIdException, IllegalAmountException;

    ShoppingCart getCartByUserId(UUID userid) throws NoCartException;

    void deleteCart(UUID userId) throws NoCartException;

}