package com.gadhub.overseasproduct.service;

public interface PaymentService {
    // 支付订单
    void payOrder(Long orderId, Long userId);
}
