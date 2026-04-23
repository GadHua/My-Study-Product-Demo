package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId
    private Long id; // 商品ID

    @TableField("name")
    private String name; // 商品名称

    @TableField("description")
    private String description; // 商品描述

    @TableField("price")
    private BigDecimal price; // 商品价格

    @TableField("stock")
    private Integer stock; // 商品库存

    @TableField("category_id")
    private Long categoryId; // 商品分类ID

    @TableField("status")
    private Integer status; // 商品状态 1: 下架 0: 上架

    @TableField("sales")
    private Integer sales; // 商品销量

    @TableField("image_url")
    private String imageUrl;

    @Version
    private Integer version; // 版本号

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt; // 创建时间

}
