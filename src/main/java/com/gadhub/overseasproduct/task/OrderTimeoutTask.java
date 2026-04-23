package com.gadhub.overseasproduct.task;

import com.gadhub.overseasproduct.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTimeoutTask {

    @Autowired
    private OrderService orderService;

    /**
     * 每5分钟执行一次，检查并取消超时未支付订单
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void cancelTimeoutOrders() {
        log.info("开始执行订单超时取消定时任务");
        try {
            orderService.cancelTimeoutOrders();
        } catch (Exception e) {
            log.error("订单超时取消定时任务执行失败", e);
        }
    }
}
