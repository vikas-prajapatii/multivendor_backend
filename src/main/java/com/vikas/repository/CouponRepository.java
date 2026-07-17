package com.vikas.repository;

import com.vikas.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
      Coupon findByCode(String code);
}
