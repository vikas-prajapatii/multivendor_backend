package com.vikas.service;

import com.vikas.model.Seller;
import com.vikas.model.SellerReports;

public interface SellerReportService {
    SellerReports getSellerReports(Seller seller);
    SellerReports updateSellerReports(SellerReports sellerReports);
}
