package com.intuit.order.model;

import com.intuit.order.enums.MeasurementUnit;
import com.intuit.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderResponse {

    private Long productOrderId;
    private Integer quantity;
    private Double pricePerUnit;
    private OrderStatus orderStatus;
    private LocalDate expiryDate;
    private LocalDate orderedOn;
    private String productName;
    private String description;
    private MeasurementUnit measurementUnit;
    private Long productId;
}
