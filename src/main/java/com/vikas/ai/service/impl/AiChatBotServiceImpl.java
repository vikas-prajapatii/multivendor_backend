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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public ApiResponse aiChatBot(String prompt, Long productId, Long userId) throws ProductException {
        if (prompt == null || prompt.trim().isEmpty()) {
            ApiResponse res = new ApiResponse();
            res.setMessage("Hello! Welcome to Noir Bazar. How can I assist your shopping today?");
            return res;
        }

        String lowerPrompt = prompt.toLowerCase().trim();

        // 1. If asking about a specific product and productId is provided
        if (productId != null) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                ApiResponse res = new ApiResponse();
                res.setMessage("✨ " + product.getTitle() + "\n\n"
                        + "• Price: ₹" + product.getSellingPrice() + " (MRP: ₹" + product.getMrpPrice() + ")\n"
                        + "• Availability: " + (product.getQuantity() > 0 ? "In Stock (" + product.getQuantity() + " units available)" : "Currently Out of Stock") + "\n"
                        + "• Color: " + (product.getColor() != null ? product.getColor() : "Standard") + "\n\n"
                        + "Would you like help adding this to your cart?");
                return res;
            }
        }

        // 2. Cart Inquiry
        if (lowerPrompt.contains("cart") || lowerPrompt.contains("basket")) {
            ApiResponse res = new ApiResponse();
            if (userId != null) {
                Cart cart = cartRepository.findByUserId(userId);
                if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
                    StringBuilder sb = new StringBuilder("🛒 You have " + cart.getCartItems().size() + " item(s) in your cart (Total: ₹" + (int) cart.getTotalSellingPrice() + "):\n\n");
                    cart.getCartItems().forEach(item -> {
                        if (item.getProduct() != null) {
                            sb.append("• ").append(item.getProduct().getTitle())
                              .append(" — Qty: ").append(item.getQuantity())
                              .append(" (₹").append(item.getSellingPrice()).append(")\n");
                        }
                    });
                    sb.append("\nYou can proceed to checkout from the cart icon at top right.");
                    res.setMessage(sb.toString().trim());
                } else {
                    res.setMessage("🛒 Your shopping cart is currently empty.\n\nExplore our latest collection to add your favorite items!");
                }
            } else {
                res.setMessage("Please sign in to view and manage your shopping cart.");
            }
            return res;
        }

        // 3. Order Tracking Inquiry
        if (lowerPrompt.contains("order") || lowerPrompt.contains("track") || lowerPrompt.contains("delivery") || lowerPrompt.contains("shipping")) {
            ApiResponse res = new ApiResponse();
            if (userId != null) {
                List<Order> orders = orderRepository.findByUserId(userId);
                if (orders != null && !orders.isEmpty()) {
                    Order latest = orders.get(orders.size() - 1);
                    res.setMessage("📦 You have placed " + orders.size() + " order(s).\n\n"
                            + "Latest Order #" + latest.getOrderId() + "\n"
                            + "• Status: " + latest.getOrderStatus() + "\n"
                            + "• Amount: ₹" + latest.getTotalSellingPrice() + "\n"
                            + "• Expected Delivery: " + (latest.getDeliveryDate() != null ? latest.getDeliveryDate().toLocalDate() : "Within 5-7 days"));
                } else {
                    res.setMessage("You haven't placed any orders yet. Place your first order today to enjoy exclusive deals!");
                }
            } else {
                res.setMessage("Please sign in to check your order history and live delivery status.");
            }
            return res;
        }

        // 4. Greeting
        if (lowerPrompt.matches("^(hi|hello|hey|greetings|namaste|good\\s*(morning|afternoon|evening)).*") || lowerPrompt.equals("hi") || lowerPrompt.equals("hello")) {
            ApiResponse res = new ApiResponse();
            res.setMessage("👋 Hello! Welcome to Noir Bazar.\n\n"
                    + "I can assist you with:\n"
                    + "• Checking product availability (e.g. \"silk saree\", \"purple saree\")\n"
                    + "• Finding Kurtas, Dresses, Menswear, or Electronics\n"
                    + "• Viewing your cart and tracking orders\n\n"
                    + "What are you looking for today?");
            return res;
        }

        // 5. Semantic Product Category / Noun Matching
        String[][] nounRules = {
            {"saree", "saree", "sari", "sarees", "saris"},
            {"kurta", "kurti", "kurtis", "kurta", "kurtas", "anarkali", "suit", "suits"},
            {"dress", "dress", "dresses", "gown", "gowns", "frock"},
            {"jeans", "jeans", "denim"},
            {"t-shirt", "t-shirt", "tshirt", "t shirt", "tee"},
            {"shirt", "shirt", "shirts"},
            {"jacket", "jacket", "jackets", "coat", "blazer"},
            {"shoe", "shoe", "shoes", "sneaker", "sneakers", "loafer", "loafers", "footwear"},
            {"laptop", "laptop", "laptops", "macbook", "computer"},
            {"mobile", "mobile", "mobiles", "phone", "phones", "smartphone", "iphone"},
            {"speaker", "speaker", "speakers", "soundbar", "audio"},
            {"carpet", "carpet", "carpets", "rug", "rugs"}
        };

        String matchedNoun = null;
        for (String[] rule : nounRules) {
            for (int i = 1; i < rule.length; i++) {
                if (lowerPrompt.contains(rule[i])) {
                    matchedNoun = rule[0];
                    break;
                }
            }
            if (matchedNoun != null) break;
        }

        String[] colors = {"blue", "red", "black", "white", "pink", "yellow", "green", "purple", "violet", "gold", "golden", "grey", "gray", "orange", "brown", "maroon", "silver"};
        String[] fabrics = {"silk", "cotton", "linen", "leather", "denim", "satin", "georgette", "chiffon", "kanjeevaram", "banarasi", "paithani", "bandhani"};

        String detectedColor = null;
        for (String c : colors) {
            if (lowerPrompt.contains(c)) {
                detectedColor = c;
                break;
            }
        }

        String detectedFabric = null;
        for (String f : fabrics) {
            if (lowerPrompt.contains(f)) {
                detectedFabric = f;
                break;
            }
        }

        if (matchedNoun != null) {
            List<Product> nounProducts = productRepository.searchProduct(matchedNoun);

            if (nounProducts == null || nounProducts.isEmpty()) {
                ApiResponse res = new ApiResponse();
                res.setMessage("We currently do not have " + matchedNoun + "s in our collection.\n\n"
                        + "✨ Noir Bazar specializes in:\n"
                        + "• Handcrafted Sarees (Kanjeevaram, Banarasi, Paithani)\n"
                        + "• Women's Ethnic Wear & Dresses\n"
                        + "• Premium Men's Fashion & Footwear\n"
                        + "• Top Electronics & Audio\n\n"
                        + "Feel free to browse our categories from the navigation bar!");
                return res;
            }

            List<Product> filtered = new java.util.ArrayList<>(nounProducts);

            if (detectedColor != null) {
                final String col = detectedColor;
                List<Product> colorMatches = filtered.stream()
                        .filter(p -> (p.getColor() != null && p.getColor().toLowerCase().contains(col))
                                || (p.getTitle() != null && p.getTitle().toLowerCase().contains(col)))
                        .collect(java.util.stream.Collectors.toList());
                if (!colorMatches.isEmpty()) {
                    filtered = colorMatches;
                } else {
                    StringBuilder sb = new StringBuilder("We currently do not have " + capitalize(detectedColor) + " " + capitalize(matchedNoun) + "s in stock.\n\n");
                    sb.append("🌸 However, we have lovely ").append(capitalize(matchedNoun)).append(" options in other colors:\n\n");
                    int count = 0;
                    for (Product p : nounProducts) {
                        if (count++ >= 4) break;
                        sb.append("• ").append(p.getTitle()).append(" – ₹").append(p.getSellingPrice());
                        if (p.getColor() != null) {
                            sb.append(" (Color: ").append(p.getColor()).append(")");
                        }
                        sb.append("\n");
                    }
                    sb.append("\n🛍️ Browse our store collection to view all available styles!");
                    ApiResponse res = new ApiResponse();
                    res.setMessage(sb.toString().trim());
                    return res;
                }
            }

            if (detectedFabric != null) {
                final String fab = detectedFabric;
                List<Product> fabricMatches = filtered.stream()
                        .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(fab))
                                || (p.getDescription() != null && p.getDescription().toLowerCase().contains(fab)))
                        .collect(java.util.stream.Collectors.toList());
                if (!fabricMatches.isEmpty()) {
                    filtered = fabricMatches;
                }
            }

            StringBuilder sb = new StringBuilder();
            String descriptor = "";
            if (detectedColor != null) descriptor += capitalize(detectedColor) + " ";
            if (detectedFabric != null) descriptor += capitalize(detectedFabric) + " ";
            descriptor += capitalize(matchedNoun) + "s";

            sb.append("✨ Yes! We have authentic ").append(descriptor).append(" available in stock:\n\n");
            int count = 0;
            for (Product p : filtered) {
                if (count++ >= 4) break;
                sb.append("• ").append(p.getTitle()).append(" – ₹").append(p.getSellingPrice());
                if (p.getQuantity() > 0) {
                    sb.append(" (In Stock)");
                }
                sb.append("\n");
            }
            sb.append("\n🛍️ You can explore and order them directly from our store collection!");
            ApiResponse res = new ApiResponse();
            res.setMessage(sb.toString().trim());
            return res;
        }

        // 6. Generic Product Keyword Search
        String cleaned = lowerPrompt
                .replaceAll("\\b(is|are|available|or|not|in|stock|do|you|have|show|me|find|the|a|an|please|can|i|get|want|to|buy|price|of|what|any)\\b", " ")
                .replaceAll("[^a-zA-Z0-9 ]", " ")
                .trim()
                .replaceAll(" +", " ");

        List<Product> genericMatches = null;
        if (!cleaned.isEmpty()) {
            genericMatches = productRepository.searchProduct(cleaned);
        }

        if (genericMatches != null && !genericMatches.isEmpty()) {
            StringBuilder sb = new StringBuilder("✨ Here are available products matching your search:\n\n");
            int count = 0;
            for (Product p : genericMatches) {
                if (count++ >= 4) break;
                sb.append("• ").append(p.getTitle()).append(" – ₹").append(p.getSellingPrice()).append("\n");
            }
            sb.append("\n🛍️ You can view them in the catalog or search by category.");
            ApiResponse res = new ApiResponse();
            res.setMessage(sb.toString().trim());
            return res;
        }

        // 7. Helpful fallback
        ApiResponse res = new ApiResponse();
        res.setMessage("I couldn't find an exact match for \"" + prompt.trim() + "\".\n\n"
                + "💡 Try asking:\n"
                + "• \"silk saree\" or \"purple saree\"\n"
                + "• \"floral kurta set\"\n"
                + "• \"men jeans\" or \"jackets\"\n"
                + "• \"laptops\" or \"speakers\"\n"
                + "• \"what is in my cart\" or \"order status\"");
        return res;
    }
}
