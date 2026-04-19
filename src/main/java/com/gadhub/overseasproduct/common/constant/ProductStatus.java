package com.gadhub.overseasproduct.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {

    ON_SHELF(0, "上架"),
    OFF_SHELF(1, "下架");

    private final Integer code;
    private final String description;
}
