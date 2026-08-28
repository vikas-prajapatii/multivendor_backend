package com.vikas.service;

import com.vikas.exception.SellerException;
import com.vikas.exception.UserException;
import com.vikas.request.LoginRequest;
import com.vikas.request.SignupRequest;
import com.vikas.response.AuthResponse;
import jakarta.mail.MessagingException;

public interface AuthService {

    void sentLoginOtp(String email) throws UserException, MessagingException;

    String createUser(SignupRequest req) throws SellerException;

    AuthResponse signing(LoginRequest req);
    AuthResponse signin(LoginRequest req) throws SellerException;

}
