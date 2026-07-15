package com.vikas.repository;

import com.vikas.model.Cart;
import com.vikas.model.CartItem;
import com.vikas.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);
}
