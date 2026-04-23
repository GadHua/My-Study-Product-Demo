package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.dto.CategoryDto;
import com.gadhub.overseasproduct.service.CategoryService;
import com.gadhub.overseasproduct.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/backend/category")
@Tag(name = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        categoryService.addCategory(categoryDto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result updateCategory(@PathVariable Long id,
                                 @RequestBody @Valid CategoryDto categoryDto) {
        categoryDto.setId(id);
        categoryService.updateCategory(categoryDto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/tree")
    public Result getCategoryTree() {
        List<CategoryTreeVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @GetMapping("/{id}")
    public Result getCategoryById(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return Result.success(category);
    }
}
