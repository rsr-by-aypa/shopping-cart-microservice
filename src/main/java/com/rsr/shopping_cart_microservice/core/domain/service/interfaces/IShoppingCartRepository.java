package com.rsr.shopping_cart_microservice.core.domain.service.interfaces;

import com.rsr.shopping_cart_microservice.core.domain.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {
    ShoppingCart findByUserId(UUID userId);
}
