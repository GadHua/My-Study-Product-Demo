package com.gadhub.overseasproduct.service;

import com.gadhub.overseasproduct.dto.ProductDto;

public interface ProductService {
    void addProduct(ProductDto productDto); // 添加商品

    void updateProduct(ProductDto productDto); // 修改商品

    void deleteProduct(Long id); // 删除商品

    ProductDto getProductById(Long id); // 根据id查询商品
}
