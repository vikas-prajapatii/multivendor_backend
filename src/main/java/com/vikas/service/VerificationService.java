package com.vikas.service;

import com.vikas.model.VerificationCode;

public interface VerificationService {
    VerificationCode createVerificationCode(String otp, String email);
}
