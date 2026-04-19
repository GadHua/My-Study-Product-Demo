package com.gadhub.overseasproduct.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id; // 商品id

    @NotBlank(message = "商品名不能为空")
    @Size(min = 1,max = 100, message = "商品名称不能为空且长度不能超过100个字符")
    private String name; // 商品名称

    @Size(min = 1,max = 500, message = "商品描述不能为空且长度不能超过200个字符")
    private String description; // 商品描述

    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格不能小于0")
    private BigDecimal price; // 商品价格

    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "商品库存不能小于0")
    private Integer stock; // 商品库存

    @NotNull(message = "商品分类id不能为空")
    private Long categoryId; // 商品分类id

    private Integer status; // 商品状态

}
