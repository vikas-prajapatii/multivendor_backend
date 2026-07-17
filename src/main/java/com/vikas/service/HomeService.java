package com.vikas.service;

import com.vikas.model.Home;
import com.vikas.model.HomeCategory;

import java.util.List;
public interface HomeService {
    public Home createHomePageData(List<HomeCategory> allCategories);

}
