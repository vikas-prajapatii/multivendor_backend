package com.vikas.controller;

import com.razorpay.PaymentLink;
import com.vikas.domain.OrderStatus;
import com.vikas.domain.PaymentMethod;
import com.vikas.domain.PaymentOrderStatus;
import com.vikas.domain.PaymentStatus;
import com.vikas.model.*;
import com.vikas.repository.CartRepository;
import com.vikas.repository.OrderRepository;
import com.vikas.repository.PaymentOrderRepository;
import com.vikas.response.PaymentLinkResponse;
import com.vikas.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController
{
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;


    @PostMapping
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(
            @RequestBody Address shippingAddress,
            @RequestParam PaymentMethod paymentMethod,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);
        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart);
        PaymentOrder paymentOrder = paymentService.createOrder(user, orders);
        PaymentLinkResponse res = new PaymentLinkResponse();
        Long primaryOrderId = orders.isEmpty() ? paymentOrder.getId() : orders.iterator().next().getId();

        if (paymentMethod.equals(PaymentMethod.COD)) {
            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.PLACED);
                order.setPaymentStatus(PaymentStatus.PENDING);
                orderRepository.save(order);
            }
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentOrder.setPaymentMethod(PaymentMethod.COD);
            paymentOrder.setPaymentLinkId("cod_" + paymentOrder.getId());
            paymentOrderRepository.save(paymentOrder);

            // Clear the cart on successful COD order
            try {
                cart.getCartItems().clear();
                cart.setTotalItem(0);
                cart.setTotalMrpPrice(0);
                cart.setTotalSellingPrice(0);
                cartRepository.save(cart);
            } catch (Exception e) {
                System.out.println("Could not clear cart: " + e.getMessage());
            }

            res.setPayment_link_url("/payment-success/" + paymentOrder.getId() + "?razorpay_payment_id=cod_" + paymentOrder.getId() + "&razorpay_payment_link_id=cod_" + paymentOrder.getId() + "&payment_method=COD&order_id=" + primaryOrderId);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            try {
                PaymentLink payment = paymentService.createRazorpayPaymentLink(
                        user,
                        paymentOrder.getAmount(),
                        paymentOrder.getId()
                );
                String paymentUrl = payment.get("short_url");
                String paymentUrlId = payment.get("id");
                res.setPayment_link_url(paymentUrl);
                paymentOrder.setPaymentLinkId(paymentUrlId);
                paymentOrderRepository.save(paymentOrder);
            } catch (Exception e) {
                System.out.println("Razorpay payment link creation failed, using dev mock fallback: " + e.getMessage());
                String mockLinkId = "mock_link_" + paymentOrder.getId();
                String mockPaymentId = "mock_pay_" + System.currentTimeMillis();
                paymentOrder.setPaymentLinkId(mockLinkId);
                paymentOrderRepository.save(paymentOrder);
                res.setPayment_link_url("/payment-success/" + paymentOrder.getId() + "?razorpay_payment_id=" + mockPaymentId + "&razorpay_payment_link_id=" + mockLinkId + "&order_id=" + primaryOrderId);
            }
        } else {
            try {
                String paymentUrl = paymentService.createStripePaymentLink(
                        user,
                        paymentOrder.getAmount(),
                        paymentOrder.getId()
                );
                res.setPayment_link_url(paymentUrl);
            } catch (Exception e) {
                System.out.println("Stripe payment link creation failed, using dev mock fallback: " + e.getMessage());
                String mockLinkId = "mock_link_" + paymentOrder.getId();
                String mockPaymentId = "mock_stripe_" + System.currentTimeMillis();
                paymentOrder.setPaymentLinkId(mockLinkId);
                paymentOrderRepository.save(paymentOrder);
                res.setPayment_link_url("/payment-success/" + paymentOrder.getId() + "?razorpay_payment_id=" + mockPaymentId + "&razorpay_payment_link_id=" + mockLinkId + "&order_id=" + primaryOrderId);
            }
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistoryHandler(

            @RequestHeader("Authorization")
            String jwt

    ) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        List<Order> orders = orderService.userOrderHistory(user.getId());

        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization")
            String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.findOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @GetMapping("/item/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(

            @PathVariable Long orderItemId,

            @RequestHeader("Authorization")
            String jwt

    ) throws Exception {

        System.out.println("-------- controller --------");

        User user = userService.findUserByJwtToken(jwt);

        OrderItem orderItem = orderService.getOrderItemById(orderItemId);

        return new ResponseEntity<>(orderItem, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization")
            String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.cancelOrder(orderId, user);

        Seller seller = sellerService.getSellerById(order.getSellerId());

        SellerReports report = sellerReportService.getSellerReports(seller);

        report.setCanceledOrders(report.getCanceledOrders() + 1);

        report.setTotalRefunds(
                report.getTotalRefunds() + order.getTotalSellingPrice()
        );

        sellerReportService.updateSellerReports(report);
        return ResponseEntity.ok(order);
    }

}
