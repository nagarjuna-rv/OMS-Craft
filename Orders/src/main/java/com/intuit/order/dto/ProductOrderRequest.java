package com.intuit.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ProductOrderRequest {

    @NotNull
    private String userId;
    private String shippingAddress;
    private String billingAddress;
    private List<ProductRequest> product;
}
