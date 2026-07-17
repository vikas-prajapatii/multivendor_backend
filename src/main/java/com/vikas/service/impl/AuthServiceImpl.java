package com.vikas.service.impl;

import com.vikas.config.JwtProvider;
import com.vikas.domain.USER_ROLE;
import com.vikas.model.Cart;
import com.vikas.model.Seller;
import com.vikas.model.User;
import com.vikas.model.VerificationCode;
import com.vikas.repository.CartRepository;
import com.vikas.repository.SellerRepository;
import com.vikas.repository.UserRepository;
import com.vikas.repository.VerificationCodeRepository;
import com.vikas.request.LoginRequest;
import com.vikas.response.AuthResponse;
import com.vikas.response.SignupRequest;
import com.vikas.service.AuthService;
import com.vikas.service.EmailService;
import com.vikas.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserServiceImpl;
    private final SellerRepository sellerRepository;

    private static final String SIGNING_PREFIX = "signing_";

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX = "signing_";
//        String SELLER_PREFIX = "seller_";
        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());
         if(role.equals(USER_ROLE.ROLE_SELLER)){
             Seller seller  = sellerRepository.findByEmail(email);

             if (seller== null) {
                 throw new Exception("seller does not exist with the provided email.");
             }
         }
           else{
             System.out.println("email"+email);
             User user = userRepository.findByEmail(email);
             if(user == null) {
                 throw new Exception("user  does not exist with the provided email.");
             }
         }


        }

        VerificationCode existingCode = verificationCodeRepository.findByEmail(email);

        if (existingCode != null) {
            verificationCodeRepository.delete(existingCode);
        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setOtp(otp);

        verificationCodeRepository.save(verificationCode);

        String subject = "Neural Noir - Login / Signup OTP";
        String text = "Your OTP is: " + otp + "\n\nDo not share this OTP with anyone.";

        emailService.sendVerificationOtpMail(email, otp, subject, text);
    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

        VerificationCode verificationCode =
                verificationCodeRepository.findByEmail(req.getEmail());


        System.out.println("================================");
        System.out.println("Request Email : " + req.getEmail());
        System.out.println("Request OTP   : " + req.getOtp());

        if (verificationCode != null) {
            System.out.println("DB Email      : " + verificationCode.getEmail());
            System.out.println("DB OTP        : " + verificationCode.getOtp());
        } else {
            System.out.println("VerificationCode is NULL");
        }
        System.out.println("================================");

        if (verificationCode == null ||
                !verificationCode.getOtp().equals(req.getOtp())) {

            throw new Exception("Wrong OTP.");
        }

        User user = userRepository.findByEmail(req.getEmail());

        if (user == null) {

            User createdUser = new User();

            createdUser.setEmail(req.getEmail());
            createdUser.setFirstName(req.getFirstName());
            createdUser.setLastName(req.getLastName());
            createdUser.setPhoneNumber("8287623613");

            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);

            // Agar SignupRequest me password hai to password use karo.
            // Agar tutorial follow kar rahe ho aur password nahi hai to OTP temporary hai.
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);

            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(
                new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString())
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);



        return jwtProvider.generateToken(authentication);


    }

    @Override
    public AuthResponse signing(LoginRequest req) {
        String  username = req.getEmail();
        String otp = req.getOtp();
        Authentication authentication = authenticate(username,otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwtToken(token);
        authResponse.setMessage("Login Success");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;
    }

    private Authentication authenticate(String username, String otp) {

        String actualEmail = username;

        if (username.startsWith("seller_")) {
            actualEmail = username.substring("seller_".length());
        }

        UserDetails userDetails = customUserServiceImpl.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }

        VerificationCode verificationCode =
                verificationCodeRepository.findByEmail(actualEmail);

        if (verificationCode == null) {
            throw new BadCredentialsException("OTP not found");
        }

        if (!verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Invalid otp");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}