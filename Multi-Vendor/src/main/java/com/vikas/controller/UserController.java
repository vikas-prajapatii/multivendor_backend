package com.vikas.controller;

import com.vikas.model.User;
import com.vikas.repository.UserRepository;
import com.vikas.response.AuthResponse;
import com.vikas.response.SignupRequest;
import com.vikas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    @GetMapping("/users/profile")
    public ResponseEntity<User> createUserHandler(
            @RequestHeader("Authorization") String jwt
    ) throws Exception{
        User user = userService.findUserByJwtToken(jwt);
        return ResponseEntity.ok(user);
        

    }
}
