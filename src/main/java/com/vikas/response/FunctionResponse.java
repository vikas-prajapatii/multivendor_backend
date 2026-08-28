package com.vikas.response;

import com.vikas.dto.OrderHistory;
import com.vikas.model.Cart;
import com.vikas.model.Product;
import lombok.Data;

@Data
public class FunctionResponse {
    private String functionName;
    private Cart userCart;
    private OrderHistory orderHistory;
    private Product product;
}
