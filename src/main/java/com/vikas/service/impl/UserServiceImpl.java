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

    @Override
    public User updateUser(User reqUser, String jwt) throws Exception {
        User existingUser = findUserByJwtToken(jwt);
        if (reqUser.getFirstName() != null && !reqUser.getFirstName().trim().isEmpty()) {
            existingUser.setFirstName(reqUser.getFirstName().trim());
        }
        if (reqUser.getLastName() != null) {
            existingUser.setLastName(reqUser.getLastName().trim());
        }
        if (reqUser.getPhoneNumber() != null && !reqUser.getPhoneNumber().trim().isEmpty()) {
            existingUser.setPhoneNumber(reqUser.getPhoneNumber().trim());
        }
        if (reqUser.getProfileImage() != null && !reqUser.getProfileImage().trim().isEmpty()) {
            existingUser.setProfileImage(reqUser.getProfileImage().trim());
        }
        return userRepository.save(existingUser);
    }
}
