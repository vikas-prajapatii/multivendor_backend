package com.vikas.service.impl;

import com.vikas.domain.USER_ROLE;
import com.vikas.model.Seller;
import com.vikas.model.User;
import com.vikas.repository.SellerRepository;
import com.vikas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class CustomUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private static final  String SELLER_PREFIX = "seller_";
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        if(username.startsWith(SELLER_PREFIX)){
            String actualUsername = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepository.findByEmail(actualUsername);

            if(seller != null){
                return buildUserDetail(seller.getEmail(),seller.getPassword(),seller.getRole());
            }
        } else{
            User user = userRepository.findByEmail(username);
            if(user != null){
                return buildUserDetail(
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole()
                );
            }
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private UserDetails buildUserDetail(String email, String password, USER_ROLE role) {
        if(role == null) role = USER_ROLE.ROLE_CUSTOMER;
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(role.toString()));
        return new org.springframework.security.core.userdetails.User(email, password, authorityList);
    }


}
