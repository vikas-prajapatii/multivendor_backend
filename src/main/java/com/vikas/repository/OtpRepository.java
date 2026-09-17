package com.vikas.repository;

import com.vikas.model.Otp;
import com.vikas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByUserOrderByCreatedAtDesc(User user);
    void deleteByUser(User user);
}
