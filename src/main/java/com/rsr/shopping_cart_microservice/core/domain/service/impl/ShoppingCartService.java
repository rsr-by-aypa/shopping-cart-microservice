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

        ShoppingCart cart;
        try {
            cart = getCartByUserId(userId);
        } catch (NoCartException e) {
            cart = new ShoppingCart();
            cart.setUserId(userId);
        }

        Optional<Item> existingItemOpt = findItemInCartByProductId(productId, cart);

        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            existingItem.setAmount(existingItem.getAmount() + amount);
        } else {
            Item newItem = new Item(productId, product.getName(), product.getPriceInEuro(), amount, product.getImageLink());
            Item persistedItem = itemRepository.save(newItem);
            cart.getItems().add(persistedItem);
        }

        changeNumberInStock(product, -amount);

        shoppingCartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(UUID userId, UUID productId)
            throws UnknownProductIdException, NoCartException {

        System.out.println("Mal schauen...");
        ShoppingCart cart = getCartByUserId(userId);
        System.out.println("Bis hierhin");
        Optional<Item> existingItemOpt = findItemInCartByProductId(productId, cart);

        if (!existingItemOpt.isPresent()) {
            throw new UnknownProductIdException();
        }

        Item existingItem = existingItemOpt.get();
        int amount = existingItem.getAmount();

        cart.getItems().remove(existingItem);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UnknownProductIdException());

        changeNumberInStock(product, amount);

        shoppingCartRepository.save(cart);
    }

    @Override
    public void updateItemInCart(UUID userId, UUID productId, int newAmount)
            throws NotEnoughInStockException, UnknownProductIdException {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new UnknownProductIdException();
        }
        Product product = productOpt.get();

        ShoppingCart cart = getCartByUserId(userId);

        Optional<Item> existingItemOpt = findItemInCartByProductId(productId, cart);

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

        changeNumberInStock(product, -amountDifference);

        shoppingCartRepository.save(cart);
    }

    private void changeNumberInStock(Product product, int amountDifference) {
        product.setNumberInStock(product.getNumberInStock() - amountDifference);
        productRepository.save(product);

        ProductAmountChangedDTO productDTO = new ProductAmountChangedDTO(product.getId(), amountDifference);
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

    private Optional<Item> findItemInCartByProductId(UUID productId, ShoppingCart cart) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }
}
