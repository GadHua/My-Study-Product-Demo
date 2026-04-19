package com.gadhub.overseasproduct.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;

    @NotBlank(message = "名称不能为空")
    @Size(max = 50, message = "名称长度不能超过50")
    private String name;

    @Min(value = 0, message = "父级ID不能小于0")
    private Long parentId;
}