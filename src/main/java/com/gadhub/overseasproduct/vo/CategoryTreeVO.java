package com.gadhub.overseasproduct.vo;

import lombok.Data;
import java.util.List;


@Data
public class CategoryTreeVO {
    private Long id;
    private String name;
    private Long parentId;
    private List<CategoryTreeVO> children;
}
