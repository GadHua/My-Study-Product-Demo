package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;

@Data
@TableName("cart")
public class Cart {
    @TableId
    private Long id;

    @TableField("user_id")
    private BigInteger userId;

    @TableField("product_id")
    private BigInteger productId;

    @TableField("quantity")
    private Integer quantity;
}
