package com.vikas.service;

import com.vikas.model.OrderItem;

public interface OrderItemService {
	OrderItem getOrderItemById(Long id) throws Exception;
}
