package com.vikas.repository;

import com.vikas.model.Product;
import com.vikas.model.Review;
import com.vikas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findReviewsByUserId(Long userId);
    List<Review> findReviewsByProductId(Long productId);
    List<Review> findByProductId(Long productId);
}
