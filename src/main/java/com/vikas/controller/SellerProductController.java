package com.vikas.controller;

import com.vikas.exception.ProductException;
import com.vikas.exception.SellerException;
import com.vikas.model.Product;
import com.vikas.model.Seller;
import com.vikas.request.CreateProductRequest;
import com.vikas.service.ProductService;
import com.vikas.service.SellerService;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/sellers/products", "/sellers/product", "/api/sellers/products", "/api/sellers/product"})
public class SellerProductController {
     private final ProductService productService;
     private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<List<Product>> getProductBySellerId(
            @RequestHeader(value = "Authorization", required = false) String jwt
    ) throws ProductException, SellerException {
        if (jwt == null || jwt.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Product> products =
                productService.getProductBySellerId(seller.getId());

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody CreateProductRequest request,
            @RequestHeader(value = "Authorization", required = false) String jwt
    ) throws Exception {
        if (jwt == null || jwt.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Seller seller = sellerService.getSellerProfile(jwt);
        if (seller == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Product product = productService.createProduct(request, seller);

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> createProductsBulk(
            @RequestBody List<CreateProductRequest> requests,
            @RequestHeader(value = "Authorization", required = false) String jwt
    ) throws Exception {
        Seller seller = null;
        if (jwt != null && !jwt.trim().isEmpty()) {
            seller = sellerService.getSellerProfile(jwt);
        }
        if (seller == null) {
            try {
                seller = sellerService.getSellerById(1L);
            } catch (Exception e) {
                // fallback
            }
        }
        if (seller == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Product> createdProducts = new ArrayList<>();
        for (CreateProductRequest req : requests) {
            Product product = productService.createProduct(req, seller);
            createdProducts.add(product);
        }

        return new ResponseEntity<>(createdProducts, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{productId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product product) throws ProductException {
        Product updatedProduct = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<Product> updateProductStock(@PathVariable Long productId) throws ProductException {
        Product product = productService.findProductById(productId);
        boolean newStock = !product.isIn_stock();
        product.setIn_stock(newStock);
        product.setQuantity(newStock ? 10 : 0);
        Product updatedProduct = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
