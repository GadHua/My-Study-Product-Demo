package com.gadhub.overseasproduct.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductListVO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private Integer status;
    private String imageUrl; // 商品图片URL
    private LocalDateTime createdAt;
}
