package com.gadhub.overseasproduct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class Category {
    @TableId
    private Long id;

    @TableField("name")
    private String name;

    @TableField("parent_id")
    private Long parentId;
}
