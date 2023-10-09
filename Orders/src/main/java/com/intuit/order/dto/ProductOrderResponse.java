package com.intuit.order.dto;

import com.intuit.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderResponse {
    private String userId;
    private Double totalAmount;
    private List<OrderProduct> products;
    private Long orderId;
    private OrderStatus orderStatus;
}
