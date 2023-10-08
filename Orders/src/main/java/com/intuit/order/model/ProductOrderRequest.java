package com.intuit.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ProductOrderRequest {

    @NotNull
    private String userId;
//    private String status;
//    private String createdBy;
//    private String updatedBy;
    private String shippingAddress;
    private String billingAddress;
    private ProductRequest product;
}
