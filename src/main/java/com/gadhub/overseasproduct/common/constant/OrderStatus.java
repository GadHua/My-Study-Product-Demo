package com.gadhub.overseasproduct.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    UNPAID(0, "待支付"),
    PAID(1, "已支付"),
    CLOSED(2, "已取消"),
    REFUNDED(3,"已退款");

    private final Integer code;
    private final String description;

}
