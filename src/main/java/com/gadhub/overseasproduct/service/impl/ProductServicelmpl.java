package com.gadhub.overseasproduct.service.impl;

import static com.gadhub.overseasproduct.common.constant.ErrorCode.PRODUCT_ID_REQUIRED;
import static com.gadhub.overseasproduct.common.constant.ErrorCode.PRODUCT_NOT_FOUND;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.common.constant.ErrorCode;
import com.gadhub.overseasproduct.common.constant.ProductStatus;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.entity.Category;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.CategoryMapper;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.ProductService;
import com.gadhub.overseasproduct.vo.ProductDetailVO;
import com.gadhub.overseasproduct.vo.ProductListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gadhub.overseasproduct.converter.ProductConverter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServicelmpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductConverter productConverter;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Page<ProductListVO> getProductPage(Integer pageNum, Integer pageSize,Long categoryId,String keyword,String sortBy,String order) {
        // 创建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);

        // 构建查询条件（可以添加排序等）
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null){
            wrapper.eq(Product::getCategoryId, categoryId);

        }

        wrapper.eq(Product::getStatus, ProductStatus.ON_SHELF.getCode());

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }

        // 动态排序
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            boolean isAsc = "asc".equalsIgnoreCase(order);

            switch (sortBy.toLowerCase()) {
                case "price":
                    if (isAsc) {
                        wrapper.orderByAsc(Product::getPrice);
                    } else {
                        wrapper.orderByDesc(Product::getPrice);
                    }
                    break;
                case "sales":
                    if (isAsc) {
                        wrapper.orderByAsc(Product::getSales);
                    } else {
                        wrapper.orderByDesc(Product::getSales);
                    }
                    break;
                case "created_at":
                case "createdAt":
                    if (isAsc) {
                        wrapper.orderByAsc(Product::getCreatedAt);
                    } else {
                        wrapper.orderByDesc(Product::getCreatedAt);
                    }
                    break;
                default:
                    // 默认排序：按创建时间降序
                    wrapper.orderByDesc(Product::getCreatedAt);
            }
        } else {
            // 默认排序：按创建时间降序
            wrapper.orderByDesc(Product::getCreatedAt);
        }

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

    @Transactional
    @Override // 添加商品
    public void addProduct(ProductDto productDto) {
        // 用converter转换
        Product product = productConverter.toEntity(productDto);
        productMapper.insert(product);
        log.info("商品添加成功, productName: {}", productDto.getName());
    }

    @Transactional
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
        log.info("商品修改成功, productId: {}", productDto.getId());
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        if (id==null){
            throw new BusinessException(PRODUCT_ID_REQUIRED);
        }



        Product product = productMapper.selectById(id);
        if (product==null){
            throw new BusinessException(PRODUCT_NOT_FOUND);
        }

        ProductDetailVO productDetailVO = productConverter.toDetailVO(product);


        if (product.getCategoryId() != null) {
            Category category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                productDetailVO.setCategoryName(category.getName());
            }
        }

        return productDetailVO;
    }

    @Transactional
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
        log.info("商品删除成功, productId: {}", id);
    }


    @Transactional
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
        log.info("商品上架成功, productId: {}", id);

    }

    @Transactional
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

        log.info("商品下架成功, productId: {}", id);
    }
}
