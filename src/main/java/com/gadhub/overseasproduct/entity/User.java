package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId
    private String id;

    @TableField("username")
    private String name;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;
// 配置自动填充时间
    @TableField(value="created_at", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
