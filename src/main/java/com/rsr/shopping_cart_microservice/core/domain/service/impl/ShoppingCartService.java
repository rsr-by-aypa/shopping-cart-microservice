package com.rsr.shopping_cart_microservice.core.domain.service.impl;

import com.rsr.shopping_cart_microservice.core.domain.model.Item;
import com.rsr.shopping_cart_microservice.core.domain.model.Product;
import com.rsr.shopping_cart_microservice.core.domain.model.ShoppingCart;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IItemRepository;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IProductRepository;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IShoppingCartRepository;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IShoppingCartService;
import com.rsr.shopping_cart_microservice.core.port.dto.ProductAmountChangedDTO;
import com.rsr.shopping_cart_microservice.core.port.dto.ProductDTO;
import com.rsr.shopping_cart_microservice.utils.exceptions.NoCartException;
import com.rsr.shopping_cart_microservice.utils.exceptions.NotEnoughInStockException;
import com.rsr.shopping_cart_microservice.utils.exceptions.UnknownProductIdException;
import com.rsr.shopping_cart_microservice.core.port.producer.StockUpdateProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ShoppingCartService implements IShoppingCartService {

    @Autowired
    private IShoppingCartRepository shoppingCartRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IItemRepository itemRepository;

    @Autowired
    private StockUpdateProducer stockUpdateProducer;

    @Override
    public void addItemToCart(UUID userId, UUID productId, int amount)
            throws NotEnoughInStockException, UnknownProductIdException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new UnknownProductIdException();
        }
        Product product = productOpt.get();
        if (product.getNumberInStock() < amount) {
            throw new NotEnoughInStockException(productId);
        }

        //das hier in äußere methode auslagern?
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new ShoppingCart();
        }
        cart.setUserId(userId);

        Optional<Item> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        log.info("Hier war ich");

        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            existingItem.setAmount(existingItem.getAmount() + amount);
        } else {
            Item newItem = new Item(productId, product.getName(), product.getPriceInEuro(), amount, product.getImageLink());
            Item persistedItem = itemRepository.save(newItem);
            cart.getItems().add(persistedItem);
        }

        product.setNumberInStock(product.getNumberInStock() - amount);
        productRepository.save(product);

        shoppingCartRepository.save(cart);

        ProductAmountChangedDTO productDTO = new ProductAmountChangedDTO(product.getId(), -amount);
//        productDTO.setId(product.getId());
//        productDTO.setName(product.getName());
//        productDTO.setPriceInEuro(product.getPriceInEuro());
//        productDTO.setNumberInStock(product.getNumberInStock());
//        productDTO.setImageLink(product.getImageLink());

        stockUpdateProducer.sendStockUpdate(productDTO);
    }

    @Override
    public void removeItemFromCart(UUID userId, UUID productId)
            throws UnknownProductIdException, NoCartException {
        ShoppingCart cart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new NoCartException(userId));

        Optional<Item> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (!existingItemOpt.isPresent()) {
            throw new UnknownProductIdException();
        }

        Item existingItem = existingItemOpt.get();
        int amount = existingItem.getAmount();

        cart.getItems().remove(existingItem);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UnknownProductIdException());

        product.setNumberInStock(product.getNumberInStock() + amount);
        productRepository.save(product);

        shoppingCartRepository.save(cart);

        ProductAmountChangedDTO productDTO = new ProductAmountChangedDTO(product.getId(), amount);
//        productDTO.setId(product.getId());
//        productDTO.setName(product.getName());
//        productDTO.setPriceInEuro(product.getPriceInEuro());
//        productDTO.setNumberInStock(product.getNumberInStock());
//        productDTO.setImageLink(product.getImageLink());

        stockUpdateProducer.sendStockUpdate(productDTO);
    }

    @Override
    public void updateItemInCart(UUID userId, UUID productId, int newAmount)
            throws NotEnoughInStockException, UnknownProductIdException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new UnknownProductIdException();
        }
        Product product = productOpt.get();

        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new ShoppingCart();
        }

        Optional<Item> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        int oldAmount = 0;
        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            oldAmount = existingItem.getAmount();
            existingItem.setAmount(newAmount);
        } else {
            Item newItem = new Item(productId, product.getName(), product.getPriceInEuro(), newAmount, product.getImageLink());
            Item persistedItem = itemRepository.save(newItem);
            cart.getItems().add(persistedItem);
        }

        int amountDifference = newAmount - oldAmount;
        if (product.getNumberInStock() < amountDifference) {
            throw new NotEnoughInStockException(productId);
        }

        product.setNumberInStock(product.getNumberInStock() - amountDifference);
        productRepository.save(product);

        shoppingCartRepository.save(cart);

        ProductAmountChangedDTO productDTO = new ProductAmountChangedDTO(product.getId(), -amountDifference);
//        productDTO.setId(product.getId());
//        productDTO.setName(product.getName());
//        productDTO.setPriceInEuro(product.getPriceInEuro());
//        productDTO.setNumberInStock(product.getNumberInStock());
//        productDTO.setImageLink(product.getImageLink());

        stockUpdateProducer.sendStockUpdate(productDTO);
    }

    @Override
    public ShoppingCart getCartByUserId(UUID userId) throws NoCartException {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);
        if (cart == null) {
            throw new NoCartException(userId);
        }
        return cart;

    }

    @Override
    public void deleteCart(UUID userId) throws NoCartException {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);
        if (cart == null) {
            throw new NoCartException(userId);
        }
        shoppingCartRepository.delete(cart);
    }

    @Override
    public double calculateTotalPrice(UUID userId) throws NoCartException {
        ShoppingCart cart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new NoCartException(userId));

        return cart.getItems().stream()
                .mapToDouble(item -> item.getPriceInEuro() * item.getAmount())
                .sum();
    }
}
