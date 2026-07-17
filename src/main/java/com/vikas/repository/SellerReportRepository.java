package com.vikas.repository;

import com.vikas.model.SellerReports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerReportRepository extends JpaRepository<SellerReports, Long> {
    SellerReports findBySellerId(Long sellerId);
}
