package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.CategoryDto;
import com.gadhub.overseasproduct.entity.Category;
import com.gadhub.overseasproduct.mapper.CategoryMapper;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.CategoryService;
import com.gadhub.overseasproduct.vo.CategoryTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void addCategory(CategoryDto categoryDto) {
        if (categoryDto.getParentId() != null && categoryDto.getParentId() != 0) {
            Category parent = categoryMapper.selectById(categoryDto.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);

        if (category.getParentId() == null) {
            category.setParentId(0L);
        }

        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(CategoryDto categoryDto) {
        if (categoryDto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category existingCategory = categoryMapper.selectById(categoryDto.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        if (categoryDto.getParentId() != null && !categoryDto.getParentId().equals(0L)) {
            if (isCircularReference(categoryDto.getId(), categoryDto.getParentId())) {
                throw new BusinessException("不能将分类设置为自己的子分类");
            }

            Category parent = categoryMapper.selectById(categoryDto.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (id == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，无法删除");
        }

        long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<com.gadhub.overseasproduct.entity.Product>()
                        .eq(com.gadhub.overseasproduct.entity.Product::getCategoryId, id)
        );
        if (productCount > 0) {
            throw new BusinessException("该分类下有商品，无法删除");
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
                .map(this::convertToTreeVO)
                .collect(Collectors.toList());

        tree.forEach(node -> fillChildren(node, allCategories));

        return tree;
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        CategoryDto dto = new CategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }

    private CategoryTreeVO convertToTreeVO(Category category) {
        CategoryTreeVO vo = new CategoryTreeVO();
        BeanUtils.copyProperties(category, vo);
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    private void fillChildren(CategoryTreeVO parent, List<Category> allCategories) {
        List<CategoryTreeVO> children = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(parent.getId()))
                .map(this::convertToTreeVO)
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
