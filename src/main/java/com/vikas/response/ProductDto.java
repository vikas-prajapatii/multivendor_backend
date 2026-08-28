package com.vikas.response;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private double mrpPrice;
    private double sellingPrice;
    private double discountPercentage;
    private String color;
    private String size;
    private int numRatings;
}
