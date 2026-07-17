package com.vikas.repository;

import com.vikas.model.User;
import com.vikas.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    WishList findByUserId(Long userId);

}
