package com.vikas.mapper;

import com.vikas.model.Product;
import com.vikas.response.ProductDto;

public class ProductMapper {
    public static ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setMrpPrice(product.getMrpPrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setColor(product.getColor());
        dto.setSize(product.getSize());
        dto.setNumRatings(product.getNumRatings());
        return dto;
    }
}
