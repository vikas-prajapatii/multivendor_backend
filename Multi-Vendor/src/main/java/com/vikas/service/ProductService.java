package com.vikas.service;

import com.vikas.exception.ProductException;
import com.vikas.model.Product;
import com.vikas.model.Seller;
import com.vikas.request.CreateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ProductService {
    public Product createProduct(CreateProductRequest req, Seller seller);
    public void deleteProduct(Long productId) throws ProductException;
    public Product updateProduct(Long productId, Product product) throws ProductException;
    Product findProductById(Long productId) throws ProductException;
    List<Product> searchProduct(String query);
    public Page<Product> getAllProducts(
      String category,
      String brand,
      String colors,
      String size,
      Integer minPrice,
      Integer maxPrice,
      String stock,
      String sort,
      Integer minDiscount,
      Integer maxDiscount,
      Integer pageNumber

    );
    List<Product> getProductBySellerId(Long sellerId);



}
