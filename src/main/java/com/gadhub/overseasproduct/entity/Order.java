package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order")
public class Order {
    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private Integer status;

    @TableField(value = "created_at",fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime payTime;
}
