package com.intuit.product.mapper;

import com.intuit.product.dto.ProductResponse;
import com.intuit.product.entity.Product;

public class ProductResponseMapper {
   public static ProductResponse mapProductEntityToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .quantityAvailable(product.getQuantityAvailable())
                .quantityUnit(product.getQuantityUnit())
                .build();
    }
}
