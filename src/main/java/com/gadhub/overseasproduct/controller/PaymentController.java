package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.annotation.RateLimit;
import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.service.PaymentService;
import com.gadhub.overseasproduct.util.UserContextUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/backend/payment")
@Tag(name = "支付管理")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 支付订单
    @PostMapping("/pay/{orderId}")
    @RateLimit(time = 60, count = 5)
    public Result payOrder(@PathVariable Long orderId) {

        Long userId = UserContextUtil.getCurrentUserId();

        paymentService.payOrder(orderId, userId);
        return Result.success("支付成功");
    }
}
