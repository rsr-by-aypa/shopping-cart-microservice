package com.rsr.shopping_cart_microservice.core.domain.service.interfaces;

import com.rsr.shopping_cart_microservice.core.domain.model.Item;
import com.rsr.shopping_cart_microservice.core.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IItemRepository extends JpaRepository<Item, UUID>  {
}
