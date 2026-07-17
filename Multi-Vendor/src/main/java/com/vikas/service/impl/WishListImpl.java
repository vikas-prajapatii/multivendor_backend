package com.vikas.service.impl;

import com.vikas.model.Product;
import com.vikas.model.User;
import com.vikas.model.WishList;
import com.vikas.repository.WishListRepository;
import com.vikas.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishListImpl implements WishListService {
    private final WishListRepository wishListRepository;

    @Override
    public WishList CreateWishList(User user) {
        WishList wishList = new WishList();
        wishList.setUser(user);

        return wishListRepository.save(wishList);
    }

    @Override
    public WishList getWishListByUserId(User user) {
        WishList wishList= wishListRepository.findByUserId(user.getId());
        if(wishList==null){
            wishList = CreateWishList(user);
        }
        return wishList;
    }

    @Override
    public WishList addProductToWishList(User user, Product product) {
        WishList wishList = getWishListByUserId(user);
        if(wishList.getProducts().contains(product)){
            wishList.getProducts().remove(product);

        }
        else wishList.getProducts().add(product);
        return wishListRepository.save(wishList);
    }
}
