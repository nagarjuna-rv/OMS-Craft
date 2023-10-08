package com.intuit.order.model;

import com.intuit.order.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Integer quantity;
    private MeasurementUnit quantityUnit;
}
