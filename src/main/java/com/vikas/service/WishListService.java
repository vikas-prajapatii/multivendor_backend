package com.vikas.service;

import com.vikas.model.Product;
import com.vikas.model.User;
import com.vikas.model.WishList;

public interface WishListService {
    WishList CreateWishList(User user);
    WishList getWishListByUserId(User user);
    WishList addProductToWishList(User user, Product product);


}
