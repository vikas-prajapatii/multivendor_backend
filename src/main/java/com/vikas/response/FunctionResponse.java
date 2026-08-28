package com.vikas.response;

import com.vikas.model.Cart;
import com.vikas.model.Order;
import com.vikas.model.Product;
import lombok.Data;
import java.util.List;

@Data
public class FunctionResponse {
    private String functionName;
    private Cart userCart;
    private List<Order> orderHistory;
    private Product product;
}
