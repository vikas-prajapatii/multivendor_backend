package com.vikas.service;

import com.vikas.exception.ReviewNotFoundException;
import com.vikas.model.Product;
import com.vikas.model.Review;
import com.vikas.model.User;
import com.vikas.request.CreateReviewRequest;

import javax.naming.AuthenticationException;
import java.util.List;

public interface ReviewService {

    Review createReview(CreateReviewRequest req,
                        User user,
                        Product product);

    List<Review> getReviewsByProductId(Long productId);
    List<Review> getReviewByProductId(Long productId);

    Review updateReview(Long reviewId,
                        String reviewText,
                        double rating,
                        Long userId) throws ReviewNotFoundException, AuthenticationException;

    void deleteReview(Long reviewId, Long userId) throws ReviewNotFoundException, AuthenticationException;

}
