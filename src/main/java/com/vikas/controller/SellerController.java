package com.vikas.controller;

import com.vikas.config.JwtProvider;
import com.vikas.domain.AccountStatus;
import com.vikas.exception.SellerException;
import com.vikas.model.Seller;
import com.vikas.model.SellerReports;
import com.vikas.model.VerificationCode;
import com.vikas.repository.SellerRepository;
import com.vikas.repository.VerificationCodeRepository;
import com.vikas.request.LoginRequest;
import com.vikas.response.AuthResponse;
import com.vikas.service.AuthService;
import com.vikas.service.EmailService;
import com.vikas.service.SellerReportService;
import com.vikas.service.SellerService;
import com.vikas.util.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.vikas.domain.USER_ROLE;
import com.vikas.request.LoginOtpRequest;
import com.vikas.response.ApiResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;
    private final EmailService emailService;
    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final SellerReportService sellerReportService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping({"/sent/login-otp", "/sent/login-top"})
    public ResponseEntity<ApiResponse<String>> sentLoginOtp(@RequestBody LoginOtpRequest req) throws Exception {
        String email = req.getEmail();
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new SellerException("No seller account registered with email: " + email);
        }

        authService.sentLoginOtp(email);

        ApiResponse<String> res = ApiResponse.<String>builder()
                .success(true)
                .status(true)
                .message("OTP sent to your email successfully")
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/verify/login-otp", "/verify/login-top", "/login"})
    public ResponseEntity<AuthResponse> loginSeller(
            @RequestBody LoginRequest req
    ) throws Exception {
        String email = req.getEmail();
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new SellerException("Seller not found with email: " + email);
        }

        if (req.getPassword() != null && !req.getPassword().isEmpty() && (req.getOtp() == null || req.getOtp().isEmpty())) {
            if (!passwordEncoder.matches(req.getPassword(), seller.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            String otp = req.getOtp();
            VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
            if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
                throw new SellerException("Wrong or expired OTP...");
            }
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_SELLER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                seller.getEmail(),
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setJwtToken(jwt);
        authResponse.setMessage("Seller login successful");
        authResponse.setRole(USER_ROLE.ROLE_SELLER);
        authResponse.setStatus(true);

        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(
            @PathVariable String otp) throws Exception {

        VerificationCode verificationCode =
                verificationCodeRepository.findByOtp(otp);

        if (verificationCode == null ||
                !verificationCode.getOtp().equals(otp)) {

            throw new Exception("wrong otp....");
        }

        Seller seller =
                sellerService.verifyEmail(verificationCode.getEmail(), otp);

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Seller> createSeller(
            @RequestBody Seller seller) throws Exception, MessagingException {

        Seller savedSeller = sellerService.createSeller(seller);

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());

        verificationCodeRepository.save(verificationCode);

        String subject = "Neural Noir Email Verification Code";
        String text = "Welcome to Neural Noir, verify your account using this link ";
        String frontend_url = "http://localhost:3000/verify-seller/";

        emailService.sendVerificationOtpMail(
                seller.getEmail(),
                verificationCode.getOtp(),
                subject,
                text + frontend_url
        );

        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Seller> getSellerById(
            @PathVariable Long id) throws SellerException {

        Seller seller = sellerService.getSellerById(id);

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(
            @RequestHeader("Authorization") String jwt)
            throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<SellerReports> getSellerReport(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        SellerReports report = sellerReportService.getSellerReports(seller);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers(
            @RequestParam(required = false) AccountStatus status) {

        List<Seller> sellers = sellerService.getAllSellers(status);

        return ResponseEntity.ok(sellers);
    }

    @PatchMapping
    public ResponseEntity<Seller> updateSeller(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Seller seller) throws Exception {

        Seller profile = sellerService.getSellerProfile(jwt);

        Seller updatedSeller =
                sellerService.updateSeller(profile.getId(), seller);

        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> deleteSeller(
            @PathVariable Long id) throws Exception {

        sellerService.deleteSeller(id);

        return ResponseEntity.noContent().build();
    }


}
