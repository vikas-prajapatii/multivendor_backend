package com.vikas.service;

import com.vikas.domain.AccountStatus;
import com.vikas.model.Seller;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SellerService {
    Seller  getSellerProfile(String jwt);
    Seller createSeller(Seller seller) throws Exception;
    Seller getSellerById(Long id) throws Exception;
    Seller getSellerByEmail(String email);
    List<Seller> getAllSellers(AccountStatus status);
    Seller updateSeller(Long id,Seller seller);
    void deleteSeller(Long id);
    Seller verifyEmail(String email,String otp) throws Exception;
    Seller updateSellerAccountStatus(Long sellerId,AccountStatus status);


}
