package com.gadhub.overseasproduct.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    private List<OrderItemDTO> items;  // 商品列表
}

