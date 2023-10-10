package com.intuit.order.dto;

import com.intuit.order.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductStockRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    private ActionType actionType = ActionType.COUNT_DECREMENT;

}
