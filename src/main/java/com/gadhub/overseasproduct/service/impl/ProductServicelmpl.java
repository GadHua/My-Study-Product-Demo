package com.gadhub.overseasproduct.service.impl;

import static com.gadhub.overseasproduct.common.constant.ErrorCode.PRODUCT_ID_REQUIRED;
import static com.gadhub.overseasproduct.common.constant.ErrorCode.PRODUCT_NOT_FOUND;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.common.constant.ProductStatus;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.ProductService;
import com.gadhub.overseasproduct.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gadhub.overseasproduct.converter.ProductConverter;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductServicelmpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductConverter productConverter;

    @Override
    public Page<ProductListVO> getProductPage(Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);

        // 构建查询条件（可以添加排序等）
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getCreatedAt);

        // 执行分页查询
        Page<Product> productPage = productMapper.selectPage(page, wrapper);

        // 转换为 VO 分页对象
        Page<ProductListVO> voPage = new Page<>();
        voPage.setCurrent(productPage.getCurrent());
        voPage.setSize(productPage.getSize());
        voPage.setTotal(productPage.getTotal());
        voPage.setPages(productPage.getPages());

        List<ProductListVO> voList = productPage.getRecords()
                .stream()
                .map(productConverter::toListVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

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


    @Override
    public void onShelf(Long id) {
        if (id==null){ // 判断商品id是否为空
            throw new BusinessException(PRODUCT_ID_REQUIRED);
        }

        Product product = productMapper.selectById(id);
        if (product == null) {  // 判断商品是否存在
            throw new BusinessException(PRODUCT_NOT_FOUND);
        }

        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id)
                .set(Product::getStatus, ProductStatus.ON_SHELF.getCode());  // 0 = 上架

        productMapper.update(null, wrapper);

    }

    @Override
    public void offShelf(Long id) {
        if (id==null){ // 判断商品id是否为空
            throw new BusinessException(PRODUCT_ID_REQUIRED);
        }

        Product product = productMapper.selectById(id);
        if (product == null) {  // 判断商品是否存在
            throw new BusinessException(PRODUCT_NOT_FOUND);
        }

        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id)
                .set(Product::getStatus, ProductStatus.OFF_SHELF.getCode());   // 0 = 下架

        productMapper.update(null, wrapper);
    }
}
