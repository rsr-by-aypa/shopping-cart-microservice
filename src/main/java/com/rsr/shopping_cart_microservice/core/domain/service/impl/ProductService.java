package com.rsr.shopping_cart_microservice.core.domain.service.impl;

import com.rsr.shopping_cart_microservice.core.domain.model.Product;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IProductRepository;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IProductService;
import com.rsr.shopping_cart_microservice.utils.exceptions.ProductIdAlreadyInUseException;
import com.rsr.shopping_cart_microservice.utils.exceptions.UnknownProductIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService implements IProductService {

    @Autowired
    private IProductRepository productRepository;

    @Override
    public void addCreatedProduct(Product product) throws ProductIdAlreadyInUseException {
        if (productRepository.existsById(product.getId())) {
            throw new ProductIdAlreadyInUseException(product.getId());
        }
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(UUID productId) throws UnknownProductIdException {
        if (!productRepository.existsById(productId)) {
            throw new UnknownProductIdException();
        }
        productRepository.deleteById(productId);
    }

    @Override
    public void changeProductNumberInStock(UUID productId, int numberInStock) throws UnknownProductIdException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new UnknownProductIdException();
        }
        Product product = productOpt.get();
        product.setNumberInStock(numberInStock);
        productRepository.save(product);
    }
}
