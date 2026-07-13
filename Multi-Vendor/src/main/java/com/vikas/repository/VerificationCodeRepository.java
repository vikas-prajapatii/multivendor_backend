package com.vikas.repository;

import com.vikas.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>{
    VerificationCode findByEmail(String email);

}