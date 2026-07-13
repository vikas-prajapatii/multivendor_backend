package com.vikas.controller;

import com.vikas.domain.USER_ROLE;
import com.vikas.model.User;
import com.vikas.model.VerificationCode;
import com.vikas.repository.UserRepository;
import com.vikas.request.LoginRequest;
import com.vikas.response.ApiResponse;
import com.vikas.response.AuthResponse;
import com.vikas.response.SignupRequest;
import com.vikas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest req ) throws Exception {
        String jwt = authService.createUser(req);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwtToken(jwt);
        authResponse.setMessage("Registered Successfully");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);


        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentOtpHandler(@RequestBody VerificationCode req) throws Exception {
        authService.sentLoginOtp(req.getEmail());

        ApiResponse apiResponse = new ApiResponse();

       apiResponse.setMessage("otp sent successfully");


        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) throws Exception {
        AuthResponse authResponse =  authService.signing(req);
        return ResponseEntity.ok(authResponse);

    }
}

