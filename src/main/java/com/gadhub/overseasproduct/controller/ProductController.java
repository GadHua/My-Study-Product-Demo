package com.gadhub.overseasproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.service.ProductService;
import com.gadhub.overseasproduct.vo.ProductListVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/backend/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add") // 添加商品
    public Result addProduct(@RequestBody @Valid ProductDto productDto) {
        productService.addProduct(productDto);
        return Result.success();
    }

    @PutMapping("/{id}") // 修改商品
    public Result updateProduct(@PathVariable Long id,@RequestBody @Valid ProductDto productDto) {
        productService.updateProduct(productDto);
        return Result.success();
    }

    @DeleteMapping("/{id}") // 删除商品
    public Result deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result getProductPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<ProductListVO> page = productService.getProductPage(pageNum, pageSize);
        return Result.success(page);
    }




    // 上架
    @PutMapping("/{id}/onShelf")
    public Result onShelf(@PathVariable Long id) {
        productService.onShelf(id);
        return Result.success();
    }

    // 下架
    @PutMapping("/{id}/offShelf")
    public Result offShelf(@PathVariable Long id) {
        productService.offShelf(id);
        return Result.success();
    }


}
