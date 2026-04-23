package com.gadhub.overseasproduct.converter;

import com.gadhub.overseasproduct.dto.CategoryDto;
import com.gadhub.overseasproduct.entity.Category;
import com.gadhub.overseasproduct.vo.CategoryTreeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryConverter {

    Category toEntity(CategoryDto categoryDto);

    CategoryDto toDto(Category category);

    @Mapping(target = "children", ignore = true)
    CategoryTreeVO toTreeVO(Category category);
}
