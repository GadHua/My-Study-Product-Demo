package com.gadhub.overseasproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CreateOrderDTO {
    private OrderItemDTO[] orderItems;
    private String receiverName;
    private String email;
}


class OrderItemDTO{
    private Long productId;
    private Integer quantity;
}
