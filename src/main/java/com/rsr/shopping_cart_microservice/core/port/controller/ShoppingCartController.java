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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@RestController
@RequestMapping("/shopping-cart")
@Tag(name = "Shopping Cart API", description = "API für den Shopping Cart Zugriff")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @Operation(summary = "Fügt Item zu Shopping Cart hinzu", description = "Fügt das angegebene Item zum Shopping Cart des entsprechenden Users hinzu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item erfolgreich hinzugefügt", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Ungültige Anfrage, da zum Beispiel das Produkt nicht existiert", content = @Content)
    })
    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addItemToCart(@PathVariable UUID userId, @RequestBody CartItemDTO cartItemDTO) {
        try {
            shoppingCartService.addItemToCart(userId, cartItemDTO.getProductId(), cartItemDTO.getAmount());
            return ResponseEntity.ok("Item added to cart");
        } catch (NotEnoughInStockException | UnknownProductIdException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Das Item im Shopping Cart wird geupdated", description = "Ein Item im Shopping Cart des angegebenen Users wird aktualisiert (Anzahl)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item erfolgreich aktualisiert", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Ungültige Anfrage, da zum Beispiel das Item nicht existiert", content = @Content)
    })
    @PostMapping("/update/{userId}")
    public ResponseEntity<String> updateItemInCart(@PathVariable UUID userId, @RequestBody CartItemDTO cartItemDTO) {
        try {
            shoppingCartService.updateItemInCart(userId, cartItemDTO.getProductId(), cartItemDTO.getAmount());
            return ResponseEntity.ok("Item quantity updated in cart");
        } catch (NotEnoughInStockException | UnknownProductIdException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ein Item wird aus dem Shopping Cart entfernt", description = "Item wird aus dem Shopping Cart des angegebenen Users entfernt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item erfolgreich entfernt", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Item konnte nicht erfolgreich entfernt werden, da es zum Beispiel nicht existiert", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/remove/{productId}/{userId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable UUID userId, @PathVariable UUID productId) {
        try {
            shoppingCartService.removeItemFromCart(userId, productId);
            return ResponseEntity.ok("Item removed from cart");
        } catch (UnknownProductIdException | NoCartException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Liefert das Shopping Cart des entsprechenden Users", description = "Das Shopping Cart des entsprechenden Users wird zurückgegeben")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shopping Cart exisitert für den User", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Shopping Cart existiert nicht", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getCartByUserId(@PathVariable UUID userId) {
        try {
            ShoppingCart cart = shoppingCartService.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (NoCartException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Löscht das Shopping Cart für den User", description = "Das Shopping Cart des entsprechenden Users wird gelöscht")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shopping Cart exisitert für den Use und wird gelöscht", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Shopping Cart kann nicht gelöscht werden", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteCart(@PathVariable UUID userId) {
        try {
            shoppingCartService.deleteCart(userId);
            return ResponseEntity.ok("Cart deleted");
        } catch (NoCartException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
