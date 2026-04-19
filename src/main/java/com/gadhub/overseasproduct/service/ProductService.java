package com.gadhub.overseasproduct.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.vo.ProductListVO;



public interface ProductService {
    void addProduct(ProductDto productDto); // 添加商品

    void updateProduct(ProductDto productDto); // 修改商品

    void deleteProduct(Long id); // 删除商品

    Page<ProductListVO> getProductPage(Integer pageNum, Integer pageSize);

    void onShelf(Long id);
    void offShelf(Long id);

}
