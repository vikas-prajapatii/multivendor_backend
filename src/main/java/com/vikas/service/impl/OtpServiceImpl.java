package com.vikas.service.impl;

import com.vikas.exception.InvalidOtpException;
import com.vikas.exception.OtpAlreadyVerifiedException;
import com.vikas.exception.OtpExpiredException;
import com.vikas.exception.UserNotFoundException;
import com.vikas.model.Otp;
import com.vikas.model.User;
import com.vikas.repository.OtpRepository;
import com.vikas.repository.UserRepository;
import com.vikas.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    @Value("${app.otp.expiry:5}")
    private int otpExpiry;

    @Override
    @Transactional
    public Otp generateAndSaveOtp(User user) {
        String code = generateOtp();
        Otp otp = Otp.builder()
                .code(code)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiry))
                .verified(false)
                .build();
        return otpRepository.save(otp);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String code) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        Otp otp = otpRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new InvalidOtpException("OTP not found"));

        if (otp.isVerified()) {
            throw new OtpAlreadyVerifiedException("OTP already verified");
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        if (!otp.getCode().equals(code)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        otp.setVerified(true);
        user.setEnabled(true);

        otpRepository.save(otp);
        userRepository.save(user);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
