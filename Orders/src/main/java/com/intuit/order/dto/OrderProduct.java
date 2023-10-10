package com.intuit.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    private Long productId;
    private String shippingAddress;
    private String billingAddress;
    private Integer quantity;
    private Double pricePerUnit;

}
