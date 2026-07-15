package com.vikas.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private int mrpPrice;
    private int sellingPrice;
    private String color;
    private List<String> images;
    private String category;
    private String category2;
    private String category3;

    private String size;
    private String title;
}
