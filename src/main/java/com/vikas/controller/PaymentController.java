package com.vikas.controller;

import com.vikas.model.*;
import com.vikas.repository.SellerRepository;
import com.vikas.repository.TransactionRepository;
import com.vikas.response.ApiResponse;
import com.vikas.response.PaymentLinkResponse;
import com.vikas.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
  private final  PaymentService paymentService;
  private final UserService userService;
  private final SellerService sellerService;
  private final SellerReportService sellerReportService;
  private final TransactionService transactionService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> paymentSuccessHandler(

            @PathVariable String paymentId,

            @RequestParam String paymentLinkId,

            @RequestHeader("Authorization") String jwt

    ) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        PaymentLinkResponse paymentResponse;

        PaymentOrder paymentOrder = paymentService
                .getPaymentOrderByPaymentId(paymentLinkId);

        boolean paymentSuccess = paymentService.proceedPaymentOrder(
                paymentOrder,
                paymentId,
                paymentLinkId
        );

        if (paymentSuccess) {
            for (Order order : paymentOrder.getOrders()) {
                 transactionService.createTransaction(order);
                Seller seller = sellerService.getSellerById(order.getSellerId());
                SellerReports report =
                        sellerReportService.getSellerReports(seller);
                report.setTotalOrders(report.getTotalOrders() + 1);
                report.setTotalEarnings(
                        report.getTotalEarnings()
                                + order.getTotalSellingPrice()
                );

                report.setTotalSales(
                        report.getTotalSales()
                                + order.getOrderItems().size()
                );

                sellerReportService.updateSellerReports(report);
            }
        }

        ApiResponse res = new ApiResponse();
        res.setMessage("Payment successful");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
