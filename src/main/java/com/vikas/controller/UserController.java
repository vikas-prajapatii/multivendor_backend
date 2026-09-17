package com.vikas.controller;

import com.vikas.model.User;
import com.vikas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/users", "/users"})
public class UserController {
    private final UserService userService;

    @GetMapping({"/profile", ""})
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        return ResponseEntity.ok(user);
    }

    @PatchMapping({"/profile", ""})
    public ResponseEntity<User> updateUserProfileHandler(
            @RequestBody User user,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User updatedUser = userService.updateUser(user, jwt);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping({"/profile", ""})
    public ResponseEntity<User> putUserProfileHandler(
            @RequestBody User user,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User updatedUser = userService.updateUser(user, jwt);
        return ResponseEntity.ok(updatedUser);
    }
}
