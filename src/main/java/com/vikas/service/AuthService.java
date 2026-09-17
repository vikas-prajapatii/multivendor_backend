package com.vikas.service;

import com.vikas.exception.SellerException;
import com.vikas.exception.UserException;
import com.vikas.request.*;
import com.vikas.response.ApiResponse;
import com.vikas.response.AuthResponse;
import com.vikas.response.LoginResponse;
import com.vikas.response.RegisterResponse;
import jakarta.mail.MessagingException;

public interface AuthService {

    ApiResponse<RegisterResponse> register(RegisterRequest request);

    ApiResponse<LoginResponse> login(LoginRequest request);

    ApiResponse<String> verifyOtp(VerifyOtpRequest request);

    ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest request);

    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<String> resetPassword(ResetPasswordRequest request);

    // Legacy multivendor support
    void sentLoginOtp(String email) throws UserException, MessagingException;

    String createUser(SignupRequest req) throws SellerException;

    AuthResponse signing(LoginRequest req);

    AuthResponse signin(LoginRequest req) throws SellerException;
}
