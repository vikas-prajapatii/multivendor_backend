package com.vikas.service;

import com.vikas.model.Otp;
import com.vikas.model.User;

public interface OtpService {
    Otp generateAndSaveOtp(User user);
    void verifyOtp(String email, String code);
}
