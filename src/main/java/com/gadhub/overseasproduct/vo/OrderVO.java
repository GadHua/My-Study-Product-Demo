package com.gadhub.overseasproduct.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime payTime;

    private List<OrderItemVO> items;  // 订单商品列表
}
