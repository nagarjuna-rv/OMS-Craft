package com.intuit.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPriceResponse {
    private Long productId;
    private Double pricePerUnit;
    private Integer quantity;
    private Double totalPrice;
}
