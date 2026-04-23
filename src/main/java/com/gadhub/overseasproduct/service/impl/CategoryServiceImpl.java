package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gadhub.overseasproduct.common.constant.ErrorCode;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.converter.CategoryConverter;
import com.gadhub.overseasproduct.dto.CategoryDto;
import com.gadhub.overseasproduct.entity.Category;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.CategoryMapper;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.CategoryService;
import com.gadhub.overseasproduct.vo.CategoryTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryConverter categoryConverter;

    @Transactional
    @Override
    public void addCategory(CategoryDto categoryDto) {
        if (categoryDto.getParentId() != null && categoryDto.getParentId() != 0) {
            Category parent = categoryMapper.selectById(categoryDto.getParentId());
            if (parent == null) {
                throw new BusinessException(ErrorCode.CATEGORY_PARENT_NOT_FOUND);
            }
        }

        Category category =  categoryConverter.toEntity(categoryDto);

        if (category.getParentId() == null) {
            category.setParentId(0L);
        }

        categoryMapper.insert(category);
        log.info("分类添加成功, categoryId: {}, name: {}", category.getId(), category.getName());

    }

    @Transactional
    @Override
    public void updateCategory(CategoryDto categoryDto) {
        if (categoryDto.getId() == null) {
            throw new BusinessException(ErrorCode.CATEGORY_ID_REQUIRED);
        }

        Category existingCategory = categoryMapper.selectById(categoryDto.getId());
        if (existingCategory == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        if (categoryDto.getParentId() != null && !categoryDto.getParentId().equals(0L)) {
            if (isCircularReference(categoryDto.getId(), categoryDto.getParentId())) {
                throw new BusinessException(ErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }

            Category parent = categoryMapper.selectById(categoryDto.getParentId());
            if (parent == null) {
                throw new BusinessException(ErrorCode.CATEGORY_PARENT_NOT_FOUND);
            }
        }

        Category category = categoryConverter.toEntity(categoryDto);
        categoryMapper.updateById(category);
        log.info("分类更新成功, categoryId: {}, name: {}", category.getId(), category.getName());
    }

    @Override
    public void deleteCategory(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.CATEGORY_ID_REQUIRED);
        }

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }

        long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getCategoryId, id)
        );
        if (productCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        List<Category> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getId)
        );

        List<CategoryTreeVO> tree = allCategories.stream()
                .filter(c -> c.getParentId() == 0 || c.getParentId() == null)
                .map(categoryConverter::toTreeVO)
                .peek(vo -> vo.setChildren(new ArrayList<>()))
                .collect(Collectors.toList());

        tree.forEach(node -> fillChildren(node, allCategories));

        return tree;
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        CategoryDto dto = categoryConverter.toDto(category);
        return dto;
    }


    private void fillChildren(CategoryTreeVO parent, List<Category> allCategories) {
        List<CategoryTreeVO> children = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(parent.getId()))
                .map(categoryConverter::toTreeVO)
                .peek(vo -> vo.setChildren(new ArrayList<>()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            children.forEach(child -> fillChildren(child, allCategories));
        }
    }

    private boolean isCircularReference(Long categoryId, Long parentId) {
        if (categoryId.equals(parentId)) {
            return true;
        }

        Long currentId = parentId;
        while (currentId != null && currentId != 0) {
            if (currentId.equals(categoryId)) {
                return true;
            }
            Category parent = categoryMapper.selectById(currentId);
            if (parent == null) {
                break;
            }
            currentId = parent.getParentId();
        }

        return false;
    }
}
