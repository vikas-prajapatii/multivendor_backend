package com.vikas.service;

import com.vikas.domain.AccountStatus;
import com.vikas.exception.SellerException;
import com.vikas.model.Seller;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SellerService {
    Seller  getSellerProfile(String jwt);
    Seller createSeller(Seller seller) throws Exception;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email);
    List<Seller> getAllSellers(AccountStatus status);
    Seller updateSeller(Long id,Seller seller) throws Exception;
    void deleteSeller(Long id) throws Exception;
    Seller verifyEmail(String email,String otp) throws Exception;
    Seller updateSellerAccountStatus(Long sellerId,AccountStatus status) throws Exception;


}
