package com.vikas.service;

import com.vikas.exception.CartItemException;
import com.vikas.exception.UserException;
import com.vikas.model.CartItem;

public interface CartItemService {
	
	CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException;
	
	void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException;
	
	CartItem findCartItemById(Long cartItemId) throws CartItemException;
	
}
