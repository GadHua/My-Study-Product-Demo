package com.gadhub.overseasproduct.service.impl;

import static com.gadhub.overseasproduct.common.constant.ErrorCode.PRODUCT_ID_REQUIRED;
import static com.gadhub.overseasproduct.common.constant.ErrorCode.PRODUCT_NOT_FOUND;

import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gadhub.overseasproduct.converter.ProductConverter;



@Service
public class ProductServicelmpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductConverter productConverter;

    @Override // 添加商品
    public void addProduct(ProductDto productDto) {
        // 用converter转换
        Product product = productConverter.toEntity(productDto);
        productMapper.insert(product);

    }

    @Override // 修改商品
    public void updateProduct(ProductDto productDto) {
        if (productDto.getId()==null){ // 判断商品id是否为空
            throw new BusinessException(PRODUCT_ID_REQUIRED);
        }

        Product existingProduct = productMapper.selectById(productDto.getId()); // 查询商品
        if (existingProduct == null) { // 判断商品是否存在
            throw new BusinessException(PRODUCT_NOT_FOUND);
        }

        // 将dto用converter转换为entity
        Product product = productConverter.toEntity(productDto);

        productMapper.updateById(product); // 对查询到的指定id进行更新

    }

    @Override // 删除商品
    public void deleteProduct(Long id) {
        if (id==null){ // 判断商品id是否为空
            throw new BusinessException(PRODUCT_ID_REQUIRED);
        }

        Product existingProduct = productMapper.selectById(id); // 查询商品
        if (existingProduct == null) { // 判断商品是否存在
            throw new BusinessException(PRODUCT_NOT_FOUND);
        }

        // 删除指定id的商品
        productMapper.deleteById(id);
    }

    @Override // 根据id查询商品
    public ProductDto getProductById(Long id) {
        if (id==null){ // 判断商品id是否为空
            throw new BusinessException(PRODUCT_ID_REQUIRED);
        }

        Product product = productMapper.selectById(id);
        if (product == null) {  // 判断商品是否存在
            throw new BusinessException(PRODUCT_NOT_FOUND);
        }

        // 将从数据库查到的entity转化为dto返回
        return productConverter.toDto(product);
    }
}
