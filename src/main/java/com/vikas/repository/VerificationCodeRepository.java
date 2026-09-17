package com.vikas.repository;

import com.vikas.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findTopByEmailOrderByIdDesc(String email);
    VerificationCode findTopByOtpOrderByIdDesc(String otp);
    List<VerificationCode> findAllByEmail(String email);
    void deleteByEmail(String email);

    default VerificationCode findByEmail(String email) {
        return findTopByEmailOrderByIdDesc(email);
    }

    default VerificationCode findByOtp(String otp) {
        return findTopByOtpOrderByIdDesc(otp);
    }
}