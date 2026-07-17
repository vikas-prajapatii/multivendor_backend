package com.vikas.service.impl;

import com.vikas.config.JwtProvider;
import com.vikas.model.User;
import com.vikas.repository.UserRepository;
import com.vikas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;


    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromToken(jwt);
        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user =  userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("user not found with email-"+email);

        }
        return user;
    }
}
