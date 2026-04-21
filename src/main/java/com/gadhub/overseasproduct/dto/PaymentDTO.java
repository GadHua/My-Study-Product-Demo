package com.gadhub.overseasproduct.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDTO {
    private Long id;
    private String payWay;
    private BigDecimal amount;
    private Long orderId;
}
