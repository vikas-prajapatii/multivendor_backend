package com.vikas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vikas.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	PasswordResetToken findByToken(String token);
}
