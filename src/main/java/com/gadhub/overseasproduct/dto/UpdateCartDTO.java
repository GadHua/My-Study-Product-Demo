package com.gadhub.overseasproduct.dto;

import lombok.Data;

@Data
public class UpdateCartDTO {
    private Integer quantity;
    private Long productId;
    private Long userId;
    private Long cartId;
}
