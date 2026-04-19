package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("product_id")
    private Long productId;

    @TableField("product_name")
    private String productName;

    @TableField("price")
    private BigDecimal productPrice;

    @TableField("quantity")
    private Integer quantity;
}
