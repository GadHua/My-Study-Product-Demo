package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gadhub.overseasproduct.common.constant.ErrorCode;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.entity.Order;
import com.gadhub.overseasproduct.entity.Payment;
import com.gadhub.overseasproduct.mapper.OrderMapper;
import com.gadhub.overseasproduct.mapper.PaymentMapper;
import com.gadhub.overseasproduct.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void payOrder(Long orderId, Long userId) {
        log.info("开始支付订单, orderId: {}, userId: {}", orderId, userId);

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getId, orderId)
                .eq(Order::getUserId, userId);
        Order order = orderMapper.selectOne(orderWrapper);
        if (order == null){
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 2. 验证订单是否已支付
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.PAYMENT_ORDER_ALREADY_PAID);
        }

        // 检查是否已有支付记录
        LambdaQueryWrapper<Payment> paymentCheckWrapper = new LambdaQueryWrapper<>();
        paymentCheckWrapper.eq(Payment::getOrderId, orderId);
        Payment existingPayment = paymentMapper.selectOne(paymentCheckWrapper);

        if (existingPayment != null){
            // 如果已有支付记录，更新它
            LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Payment::getId, existingPayment.getId())
                    .set(Payment::getStatus, 1)
                    .set(Payment::getPayTime, LocalDateTime.now())
                    .set(Payment::getTransactionId, "TXN" + System.currentTimeMillis());
            paymentMapper.update(null, updateWrapper);


            LambdaUpdateWrapper<Order> orderUpdateWrapper = new LambdaUpdateWrapper<>();
            orderUpdateWrapper.eq(Order::getId, orderId).set(Order::getStatus, 1)
                    .set(Order::getPayTime, LocalDateTime.now());

            orderMapper.update(null, orderUpdateWrapper);

        }else{
            // 3. 创建支付记录（待支付）
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(order.getTotalAmount());
            payment.setPayWay("wechat");
            payment.setStatus(0); // 0=待支付
            paymentMapper.insert(payment);

            // 4. 模拟支付成功（假装接收到第三方回调）
            LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Payment::getId, payment.getId())
                    .set(Payment::getStatus, 1)  // 1=支付成功
                    .set(Payment::getPayTime, LocalDateTime.now())
                    .set(Payment::getTransactionId, "TXN" + System.currentTimeMillis());
            paymentMapper.update(null, updateWrapper);

            // 5. 更新订单状态
            LambdaUpdateWrapper<Order> orderUpdateWrapper = new LambdaUpdateWrapper<>();
            orderUpdateWrapper.eq(Order::getId, orderId)
                    .set(Order::getStatus, 1)  // 1=已支付
                    .set(Order::getPayTime, LocalDateTime.now());
            orderMapper.update(null, orderUpdateWrapper);

        }
        log.info("订单支付成功, orderId: {}, amount: {}", orderId, order.getTotalAmount());


    }
}
