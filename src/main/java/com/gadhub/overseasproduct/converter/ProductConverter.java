package com.gadhub.overseasproduct.converter;

import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(ProductDto productDto);

    ProductDto toDto(Product product);
}
