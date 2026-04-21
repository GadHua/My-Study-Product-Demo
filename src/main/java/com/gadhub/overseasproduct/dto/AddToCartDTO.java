package com.gadhub.overseasproduct.dto;

import lombok.Data;

@Data
public class AddToCartDTO {
    private Long productId;
    private Integer quantity;

}
