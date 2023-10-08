package com.intuit.product.service;

import com.intuit.product.dto.ProductPriceResponse;
import com.intuit.product.dto.ProductRequest;
import com.intuit.product.dto.ProductResponse;
import com.intuit.product.dto.ProductStockRequest;
import com.intuit.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product addProduct(ProductRequest productRequest);
    ProductResponse updateProductDetails(Long productId, Product product);

    Product updateProductStock(ProductStockRequest stock);

    ProductResponse deleteProduct(Long productId);
    List<ProductResponse> getAllProduct();
    Optional<ProductResponse> getProductById(Long Id);

    ProductPriceResponse getPriceQuoteByProductId(Long Id, Integer quantity);



}
