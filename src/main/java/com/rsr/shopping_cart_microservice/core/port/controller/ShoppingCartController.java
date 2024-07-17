package com.rsr.shopping_cart_microservice.core.port.controller;

import com.rsr.shopping_cart_microservice.core.domain.model.ShoppingCart;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IShoppingCartService;
import com.rsr.shopping_cart_microservice.core.port.dto.CartItemDTO;
import com.rsr.shopping_cart_microservice.utils.exceptions.NoCartException;
import com.rsr.shopping_cart_microservice.utils.exceptions.NotEnoughInStockException;
import com.rsr.shopping_cart_microservice.utils.exceptions.UnknownProductIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addItemToCart(@PathVariable UUID userId, @RequestBody CartItemDTO cartItemDTO) {
        try {
            shoppingCartService.addItemToCart(userId, cartItemDTO.getProductId(), cartItemDTO.getAmount());
            return ResponseEntity.ok("Item added to cart");
        } catch (NotEnoughInStockException | UnknownProductIdException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<String> updateItemInCart(@PathVariable UUID userId, @RequestBody CartItemDTO cartItemDTO) {
        try {
            shoppingCartService.updateItemInCart(userId, cartItemDTO.getProductId(), cartItemDTO.getAmount());
            return ResponseEntity.ok("Item quantity updated in cart");
        } catch (NotEnoughInStockException | UnknownProductIdException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}/{userId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable UUID userId, @PathVariable UUID productId) {
        try {
            shoppingCartService.removeItemFromCart(userId, productId);
            return ResponseEntity.ok("Item removed from cart");
        } catch (UnknownProductIdException | NoCartException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getCartByUserId(@PathVariable UUID userId) {
        try {
            ShoppingCart cart = shoppingCartService.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (NoCartException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteCart(@PathVariable UUID userId) {
        try {
            shoppingCartService.deleteCart(userId);
            return ResponseEntity.ok("Cart deleted");
        } catch (NoCartException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<Double> getTotalPrice(@PathVariable UUID userId) {
        try {
            double totalPrice = shoppingCartService.calculateTotalPrice(userId);
            return ResponseEntity.ok(totalPrice);
        } catch (NoCartException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
