package com.gadhub.overseasproduct.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;

}
