package com.vikas.service.impl;

import com.vikas.model.Seller;
import com.vikas.model.SellerReports;
import com.vikas.repository.SellerReportRepository;
import com.vikas.service.SellerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {
    private final SellerReportRepository sellerReportRepository;
    @Override
    public SellerReports getSellerReports(Seller seller) {
        SellerReports sr = sellerReportRepository.findBySellerId(seller.getId());
        if(sr == null){
            SellerReports newSellerReports = new SellerReports();
            newSellerReports.setSeller(seller);
            return sellerReportRepository.save(newSellerReports);
        }
        return sr;
    }

    @Override
    public SellerReports updateSellerReports(SellerReports sellerReports) {
        return sellerReportRepository.save(sellerReports);
    }
}
