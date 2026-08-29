package com.vikas.service;

import com.vikas.model.HomeCategory;

import java.util.List;

public interface HomeCategoryService {
    HomeCategory createCategory(HomeCategory categories);
    List<HomeCategory> createCategories(List<HomeCategory> categories);
    List<HomeCategory> getAllCategories();
    List<HomeCategory> getAllHomeCategories();
    HomeCategory updateCategory(HomeCategory categories, Long id) throws Exception;
    HomeCategory updateHomeCategory(HomeCategory categories, Long id) throws Exception;
}
