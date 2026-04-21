package com.gadhub.overseasproduct.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartVO {
    private Long cartId;        // 购物车ID
    private Long productId;     // 商品ID
    private String productName; // 商品名称
    private BigDecimal price;   // 商品价格
    private Integer quantity;   // 数量
    private BigDecimal subtotal; // 小计（价格 × 数量）
}
