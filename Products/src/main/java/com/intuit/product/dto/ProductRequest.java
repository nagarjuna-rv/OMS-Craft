package com.intuit.product.dto;

import com.intuit.product.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private Double price;
    private String description;
    private Integer quantityAvailable;
    private MeasurementUnit quantityUnit;
}
