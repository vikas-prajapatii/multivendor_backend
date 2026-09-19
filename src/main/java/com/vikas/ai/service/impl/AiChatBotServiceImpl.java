package com.vikas.ai.service.impl;

import com.vikas.ai.service.AiChatBotService;
import com.vikas.exception.ProductException;
import com.vikas.mapper.OrderMapper;
import com.vikas.mapper.ProductMapper;
import com.vikas.model.Cart;
import com.vikas.model.Order;
import com.vikas.model.Product;
import com.vikas.model.User;
import com.vikas.repository.CartRepository;
import com.vikas.repository.OrderRepository;
import com.vikas.repository.ProductRepository;
import com.vikas.repository.UserRepository;
import com.vikas.response.ApiResponse;
import com.vikas.response.FunctionResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatBotServiceImpl implements AiChatBotService {

    String GEMINI_API_KEY = "AIzaSyDp-jeRRqqbr08scpIn1p9rLEL_Nqv5Zuo";

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private JSONArray createFunctionDeclarations() {
        return new JSONArray()
                .put(new JSONObject()
                        .put("name", "getUserCart")
                        .put("description", "Retrieve the user's cart details")
                        .put("parameters", new JSONObject()
                                .put("type", "OBJECT")
                                .put("properties", new JSONObject()
                                        .put("cart", new JSONObject()
                                                .put("type", "STRING")
                                                .put("description", "Cart Details, like total item in cart, cart item, remove item from cart, cart Id")
                                        )
                                )
                                .put("required", new JSONArray()
                                        .put("cart")
                                )
                        )
                )
                .put(new JSONObject()
                        .put("name", "getUsersOrder")
                        .put("description", "Retrieve the user's order details")
                        .put("parameters", new JSONObject()
                                .put("type", "OBJECT")
                                .put("properties", new JSONObject()
                                        .put("order", new JSONObject()
                                                .put("type", "STRING")
                                                .put("description", "Order Details, order, total order, current order, delivered order, pending order, current order, cancled order")
                                        )
                                )
                                .put("required", new JSONArray()
                                        .put("order")
                                )
                        )
                )
                .put(new JSONObject()
                        .put("name", "getProductDetails")
                        .put("description", "Retrieve product details")
                        .put("parameters", new JSONObject()
                                .put("type", "OBJECT")
                                .put("properties", new JSONObject()
                                        .put("product", new JSONObject()
                                                .put("type", "STRING")
                                                .put("description", "The Product Details like, Product title, product id, product color, product size, selling price, mrp price, rating extra...")
                                        )
                                )
                                .put("required", new JSONArray()
                                        .put("product")
                                )
                        )
                );
    }

    private FunctionResponse processFunctionCall(JSONObject functionCall,
                                                 Long productId,
                                                 Long userId
    ) throws ProductException {
        String functionName = functionCall.getString("name");
        JSONObject args = functionCall.getJSONObject("args");

        FunctionResponse res = new FunctionResponse();
        res.setFunctionName(functionName);
        User user = userRepository.findById(userId).orElse(null);

        switch (functionName) {
            case "getUserCart":
                Cart cart = cartRepository.findByUserId(userId);
                System.out.println("cart: " + cart.getId());
                res.setUserCart(cart);
                break;
            case "getUsersOrder":
                List<Order> orders = orderRepository.findByUserId(userId);
                res.setOrderHistory(OrderMapper.toOrderHistory(orders, user));
                System.out.println("order history: " + OrderMapper.toOrderHistory(orders, user));
                break;
            case "getProductDetails":
                Product product = productRepository.findById(productId).orElseThrow(
                        () -> new ProductException("product not found")
                );
                res.setProduct(product);
                break;
            default:
                throw new IllegalArgumentException("Unsupported function: " + functionName);
        }
        return res;
    }

    public FunctionResponse getFunctionResponse(String prompt, Long productId, Long userId) throws ProductException {
        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;

        JSONObject requestBodyJson = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text", prompt)
                                        )
                                )
                        )
                ).put("tools", new JSONArray()
                        .put(new JSONObject()
                                .put("functionDeclarations", createFunctionDeclarations())
                        )
                );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);

        String responseBody = response.getBody();
        JSONObject jsonObject = new JSONObject(responseBody);

        System.out.println("functionResponse: " + responseBody);
        JSONArray candidates = jsonObject.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject firstPart = parts.getJSONObject(0);
        JSONObject functionCall = firstPart.getJSONObject("functionCall");

        return processFunctionCall(functionCall, productId, userId);
    }

    @Override
    public ApiResponse aiChatBot(String prompt, Long productId, Long userId) throws ProductException {
        if (prompt == null || prompt.trim().isEmpty()) {
            ApiResponse res = new ApiResponse();
            res.setMessage("Hello! Welcome to Noir Bazar. How can I assist you with your shopping today?");
            return res;
        }

        String lowerPrompt = prompt.toLowerCase().trim();

        // 1. If asking about a specific product and productId is provided
        if (productId != null) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                ApiResponse res = new ApiResponse();
                res.setMessage("The \"" + product.getTitle() + "\" is priced at ₹" + product.getSellingPrice() 
                        + (product.getQuantity() > 0 ? " and is in stock (" + product.getQuantity() + " available)." : " but is currently out of stock.")
                        + " Color: " + (product.getColor() != null ? product.getColor() : "Multi") + ".");
                return res;
            }
        }

        // 2. Check if asking about Cart
        if (lowerPrompt.contains("cart") || lowerPrompt.contains("basket")) {
            ApiResponse res = new ApiResponse();
            if (userId != null) {
                Cart cart = cartRepository.findByUserId(userId);
                if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
                    StringBuilder sb = new StringBuilder("You currently have " + cart.getCartItems().size() + " item(s) in your cart (Total: ₹" + (int) cart.getTotalSellingPrice() + "):\n");
                    cart.getCartItems().forEach(item -> {
                        if (item.getProduct() != null) {
                            sb.append("• ").append(item.getProduct().getTitle()).append(" - Qty: ").append(item.getQuantity()).append(" (₹").append(item.getSellingPrice()).append(")\n");
                        }
                    });
                    res.setMessage(sb.toString().trim());
                } else {
                    res.setMessage("Your shopping cart is currently empty. Feel free to browse our collection and add your favorite items!");
                }
            } else {
                res.setMessage("Please sign in to view and manage your cart items.");
            }
            return res;
        }

        // 3. Check if asking about Orders
        if (lowerPrompt.contains("order") || lowerPrompt.contains("track") || lowerPrompt.contains("delivery")) {
            ApiResponse res = new ApiResponse();
            if (userId != null) {
                List<Order> orders = orderRepository.findByUserId(userId);
                if (orders != null && !orders.isEmpty()) {
                    Order latest = orders.get(orders.size() - 1);
                    res.setMessage("You have " + orders.size() + " order(s). Your latest order #" + latest.getOrderId() 
                            + " has status: " + latest.getOrderStatus() + " (Total: ₹" + latest.getTotalSellingPrice() + ").");
                } else {
                    res.setMessage("You haven't placed any orders yet. Check out our latest deals to place your first order!");
                }
            } else {
                res.setMessage("Please sign in to view your order history and tracking status.");
            }
            return res;
        }

        // 4. Product Search & Availability Check (e.g. "blue saree is available or not", "saree", "kurta", "laptop")
        String cleaned = lowerPrompt
                .replaceAll("\\b(is|are|available|or|not|in|stock|do|you|have|show|me|find|the|a|an|please|can|i|get|want|to|buy|price|of)\\b", " ")
                .replaceAll("[^a-zA-Z0-9 ]", " ")
                .trim()
                .replaceAll(" +", " ");

        List<Product> matchedProducts = null;
        if (!cleaned.isEmpty()) {
            matchedProducts = productRepository.searchProduct(cleaned);
        }

        // Try single keywords if multi-word phrase didn't match
        if ((matchedProducts == null || matchedProducts.isEmpty()) && !cleaned.isEmpty()) {
            String[] words = cleaned.split(" ");
            for (String word : words) {
                if (word.length() >= 3) {
                    matchedProducts = productRepository.searchProduct(word);
                    if (matchedProducts != null && !matchedProducts.isEmpty()) {
                        break;
                    }
                }
            }
        }

        if (matchedProducts != null && !matchedProducts.isEmpty()) {
            StringBuilder sb = new StringBuilder("Yes! We have available products matching your search:\n");
            int count = 0;
            for (Product p : matchedProducts) {
                if (count++ >= 4) break;
                sb.append("• ").append(p.getTitle()).append(" - ₹").append(p.getSellingPrice());
                if (p.getQuantity() > 0) {
                    sb.append(" (In Stock)");
                }
                sb.append("\n");
            }
            sb.append("\nYou can browse them directly in our store collection!");
            ApiResponse res = new ApiResponse();
            res.setMessage(sb.toString().trim());
            return res;
        }

        // 5. Greeting
        if (lowerPrompt.contains("hello") || lowerPrompt.contains("hi") || lowerPrompt.contains("hey")) {
            ApiResponse res = new ApiResponse();
            res.setMessage("Hello! Welcome to Noir Bazar. I can help you check product availability, sarees, dresses, cart details, and order tracking. How can I help you today?");
            return res;
        }

        // 6. Helpful default response
        ApiResponse res = new ApiResponse();
        res.setMessage("Currently, we couldn't find an exact match for \"" + prompt.trim() + "\", but we have 40+ exquisite sarees, ethnic wear, and deals in our store! Browse our Women's section or ask for specific styles.");
        return res;
    }
}
