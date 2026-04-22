package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.service.PaymentService;
import com.gadhub.overseasproduct.util.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/backend/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 支付订单
    @PostMapping("/pay/{orderId}")
    public Result payOrder(@PathVariable Long orderId) {

        Long userId = UserContextUtil.getCurrentUserId();

        paymentService.payOrder(orderId, userId);
        return Result.success("支付成功");
    }
}
