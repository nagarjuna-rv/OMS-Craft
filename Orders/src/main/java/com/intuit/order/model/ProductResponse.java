package com.intuit.order.model;

import com.intuit.order.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String name;
    private Double price;
    private String description;
    private Integer quantityAvailable;
    @Enumerated(EnumType.STRING)
    private MeasurementUnit quantityUnit;
}
