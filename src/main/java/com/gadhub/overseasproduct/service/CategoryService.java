package com.gadhub.overseasproduct.service;

import com.gadhub.overseasproduct.dto.CategoryDto;
import com.gadhub.overseasproduct.vo.CategoryTreeVO;
import java.util.List;

public interface CategoryService {

    void addCategory(CategoryDto categoryDto); // 添加分类

    void updateCategory(CategoryDto categoryDto); // 修改分类

    void deleteCategory(Long id); // 删除分类

    List<CategoryTreeVO> getCategoryTree(); // 获取分类树

    CategoryDto getCategoryById(Long id); // 根据ID获取分类
}
