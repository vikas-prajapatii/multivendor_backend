package com.vikas.service;

import com.vikas.model.Cart;
import com.vikas.model.Coupon;
import com.vikas.model.User;

import java.util.List;

public interface CouponService {
    Cart applyCoupon(String code, double orderValue, User user) throws Exception;
    Cart removeCoupon(String code, User user) throws Exception;
    Coupon findCouponById(Long id) throws Exception;
    Coupon getCouponById(Long couponId);
    
    Coupon createCoupon(Coupon coupon);
    void deleteCoupon(Long couponId);
    List<Coupon> getAllCoupons();
    List<Coupon> findAllCoupons();
}
