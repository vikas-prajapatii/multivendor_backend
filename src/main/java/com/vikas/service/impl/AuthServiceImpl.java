package com.vikas.service.impl;

import com.vikas.config.JwtProvider;
import com.vikas.config.JwtService;
import com.vikas.domain.AuthProvider;
import com.vikas.domain.USER_ROLE;
import com.vikas.exception.*;
import com.vikas.model.Cart;
import com.vikas.model.Otp;
import com.vikas.model.User;
import com.vikas.model.VerificationCode;
import com.vikas.repository.CartRepository;
import com.vikas.repository.UserRepository;
import com.vikas.repository.VerificationCodeRepository;
import com.vikas.request.*;
import com.vikas.response.ApiResponse;
import com.vikas.response.AuthResponse;
import com.vikas.response.LoginResponse;
import com.vikas.response.RegisterResponse;
import com.vikas.service.AuthService;
import com.vikas.service.EmailService;
import com.vikas.service.OtpService;
import com.vikas.service.UserService;
import com.vikas.util.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProvider jwtProvider;
    private final CustomUserServiceImpl customUserDetails;
    private final CartRepository cartRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserService userService;

    // --- Authflow Implementation ---

    @Override
    @Transactional
    public ApiResponse<RegisterResponse> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobile(request.getMobile());
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setProvider(AuthProvider.LOCAL);
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        Otp otp = otpService.generateAndSaveOtp(savedUser);
        emailService.sendOtp(savedUser.getEmail(), otp.getCode());

        RegisterResponse registerResponse = RegisterResponse.builder()
                .email(savedUser.getEmail())
                .message("User registered successfully. Please verify your OTP to activate account.")
                .build();

        return ApiResponse.<RegisterResponse>builder()
                .success(true)
                .status(true)
                .message("User registered successfully")
                .data(registerResponse)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> verifyOtp(VerifyOtpRequest request) {
        otpService.verifyOtp(request.getEmail(), request.getOtp());

        User user = userRepository.findByEmail(request.getEmail());
        if (user != null && cartRepository.findByUserId(user.getId()) == null) {
            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        return ApiResponse.<String>builder()
                .success(true)
                .status(true)
                .message("Email verified successfully")
                .data("Account activated")
                .build();
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = customUserDetails.loadUserByUsername(request.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail());
        USER_ROLE role = user != null ? user.getRole() : USER_ROLE.ROLE_CUSTOMER;

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .role(role)
                .email(request.getEmail())
                .message("Login successful")
                .build();

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .status(true)
                .message("Login successful")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<LoginResponse> refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new InvalidTokenException("Invalid or expired Refresh Token");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);

        User user = userRepository.findByEmail(username);
        USER_ROLE role = user != null ? user.getRole() : USER_ROLE.ROLE_CUSTOMER;

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .role(role)
                .email(username)
                .message("Access token refreshed")
                .build();

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .status(true)
                .message("Access token generated")
                .data(response)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + request.getEmail());
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendPasswordResetLink(user.getEmail(), resetLink);

        return ApiResponse.<String>builder()
                .success(true)
                .status(true)
                .message("Password reset link sent to your email")
                .data("Reset email sent")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ApiResponse.<String>builder()
                .success(true)
                .status(true)
                .message("Password reset successfully")
                .data("Password updated")
                .build();
    }

    // --- Legacy Multivendor Support ---

    @Override
    public void sentLoginOtp(String email) throws UserException, MessagingException {
        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());
            try {
                userService.findUserByEmail(email);
            } catch (Exception e) {
                throw new UserException(e.getMessage());
            }
        }

        List<VerificationCode> isExist = verificationCodeRepository.findAllByEmail(email);
        if (!isExist.isEmpty()) {
            verificationCodeRepository.deleteAll(isExist);
        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "Noir Bazaar Login/Signup Otp";
        String text = "Your login OTP is - " + otp;
        emailService.sendVerificationOtpMail(email, otp, subject, text);
    }

    @Override
    public String createUser(SignupRequest req) throws SellerException {
        String email = req.getEmail();
        String fullName = req.getFullName();
        String otp = req.getOtp();

        if (userRepository.existsByEmail(email)) {
            throw new SellerException("Email already registered with another account");
        }

        // If OTP is provided, verify it
        if (otp != null && !otp.trim().isEmpty()) {
            VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
            if (verificationCode == null || !verificationCode.getOtp().equals(otp.trim())) {
                throw new SellerException("Wrong OTP...");
            }
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
        createdUser.setMobile("9083476123");
        String rawPassword = req.getPassword() != null && !req.getPassword().isEmpty() ? req.getPassword() : (otp != null ? otp : "123456");
        createdUser.setPassword(passwordEncoder.encode(rawPassword));
        createdUser.setEnabled(true);

        User savedUser = userRepository.save(createdUser);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        UserDetails userDetails = customUserDetails.loadUserByUsername(email);
        return jwtService.generateAccessToken(userDetails);
    }

    @Override
    public AuthResponse signing(LoginRequest req) {
        try {
            return signin(req);
        } catch (SellerException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    @Override
    public AuthResponse signin(LoginRequest req) throws SellerException {
        String username = req.getEmail();
        String otp = req.getOtp();

        // If password is provided and otp is null or empty, authenticate with password
        if ((otp == null || otp.trim().isEmpty()) && (req.getPassword() != null && !req.getPassword().isEmpty())) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, req.getPassword())
            );
            UserDetails userDetails = customUserDetails.loadUserByUsername(username);
            String token = jwtService.generateAccessToken(userDetails);

            User user = userRepository.findByEmail(username);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Login Success");
            authResponse.setJwt(token);
            authResponse.setJwtToken(token);
            authResponse.setStatus(true);
            authResponse.setRole(user != null ? user.getRole() : USER_ROLE.ROLE_CUSTOMER);
            return authResponse;
        }

        // Otherwise use OTP authentication
        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
        String token = jwtService.generateAccessToken(userDetails);
        User user = userRepository.findByEmail(username);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        authResponse.setJwtToken(token);
        authResponse.setStatus(true);
        authResponse.setRole(user != null ? user.getRole() : USER_ROLE.ROLE_CUSTOMER);

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) throws SellerException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("Wrong OTP...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}