package com.vikas.service;

import com.vikas.model.Cart;
import com.vikas.model.CartItem;
import com.vikas.model.Product;
import com.vikas.model.User;

public interface CartService {
     public CartItem addCartItem(
        User user,
        Product product,
        String size,
        int quantity
     );
     public Cart findUserCart(User user);

}
