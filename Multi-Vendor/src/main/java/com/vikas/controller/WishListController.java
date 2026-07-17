package com.vikas.controller;

import com.vikas.model.Product;
import com.vikas.model.User;
import com.vikas.repository.WishListRepository;
import com.vikas.service.ProductService;
import com.vikas.service.UserService;
import com.vikas.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vikas.model.WishList;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishListController {
    private final WishListService wishListService;
    private final UserService userService;
    private final ProductService productService;
    @GetMapping
    public ResponseEntity<WishList> getWishlistByUserId(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        WishList wishlist = wishListService.getWishListByUserId(user);
        return ResponseEntity.ok(wishlist);
    }
    @PostMapping("/add-product/{productId}")
    public ResponseEntity<WishList> addProductToWishlist(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        Product product = productService.findProductById(productId);
        User user = userService.findUserByJwtToken(jwt);
        WishList updatedWishlist = wishListService.addProductToWishList(user, product);
        return ResponseEntity.ok(updatedWishlist);
    }

}
