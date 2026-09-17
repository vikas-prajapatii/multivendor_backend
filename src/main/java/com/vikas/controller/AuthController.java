package com.vikas.controller;

import com.vikas.domain.USER_ROLE;
import com.vikas.model.User;
import com.vikas.repository.UserRepository;
import com.vikas.request.*;
import com.vikas.response.ApiResponse;
import com.vikas.response.AuthResponse;
import com.vikas.response.LoginResponse;
import com.vikas.response.RegisterResponse;
import com.vikas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // --- Authflow Standard Endpoints ---

    @PostMapping("/api/auth/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/api/auth/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/api/auth/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/api/auth/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @GetMapping("/api/auth/profile")
    public ResponseEntity<ApiResponse<User>> profile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(ApiResponse.<User>builder()
                    .success(false)
                    .message("Anonymous")
                    .build());
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true)
                .status(true)
                .message("Profile fetched successfully")
                .data(user)
                .build());
    }

    // --- Multivendor Legacy Endpoints ---

    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest req) throws Exception {
        String jwt = authService.createUser(req);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwtToken(jwt);
        authResponse.setJwt(jwt);
        authResponse.setMessage("Registered Successfully");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);
        authResponse.setStatus(true);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/auth/sent/login-signup-otp")
    public ResponseEntity<ApiResponse<String>> sentOtpHandler(@RequestBody LoginOtpRequest req) throws Exception {
        authService.sentLoginOtp(req.getEmail());
        ApiResponse<String> res = ApiResponse.<String>builder()
                .success(true)
                .status(true)
                .message("OTP sent successfully")
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) throws Exception {
        AuthResponse authResponse = authService.signing(req);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtpLegacy(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/auth/reset-password-request")
    public ResponseEntity<ApiResponse<String>> resetPasswordRequestHandler(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPasswordLegacy(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
