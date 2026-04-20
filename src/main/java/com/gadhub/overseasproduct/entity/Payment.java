package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {
    @TableId
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("pay_way")
    private String payWay;

    @TableField("status")
    private Integer status;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("transaction_id")
    private String transactionId;
}
