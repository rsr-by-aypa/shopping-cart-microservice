package com.rsr.shopping_cart_microservice.core.domain.service.interfaces;

import com.rsr.shopping_cart_microservice.core.domain.model.Product;
import com.rsr.shopping_cart_microservice.utils.exceptions.ProductIdAlreadyInUseException;
import com.rsr.shopping_cart_microservice.utils.exceptions.UnknownProductIdException;

import java.util.UUID;

public interface IProductService {

    void addCreatedProduct(Product product) throws ProductIdAlreadyInUseException;

    void deleteProduct(UUID productId) throws UnknownProductIdException;

    void changeProductNumberInStock(UUID productId, int subtractFromAmount) throws UnknownProductIdException, IllegalArgumentException;

}
