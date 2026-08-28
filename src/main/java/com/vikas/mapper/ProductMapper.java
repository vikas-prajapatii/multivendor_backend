package com.vikas.mapper;

import com.vikas.dto.ProductDto;
import com.vikas.model.Product;

public class ProductMapper {

    public static ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setTitle(product.getTitle());
        productDto.setDescription(product.getDescription());
        productDto.setMrpPrice((int) product.getMrpPrice());
        productDto.setSellingPrice((int) product.getSellingPrice());
        productDto.setDiscountPercent(product.getDiscountPercent());
        productDto.setQuantity(product.getQuantity());
        productDto.setColor(product.getColor());
        productDto.setImages(product.getImages());
        productDto.setNumRatings(product.getNumRatings());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setSizes(product.getSizes());

        return productDto;
    }

    public Product mapToEntity(ProductDto productDto) {
        return null;
    }
}
