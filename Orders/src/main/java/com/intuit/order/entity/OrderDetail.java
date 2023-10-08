package com.intuit.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderDetail {
    @Id
    @SequenceGenerator(name = "user_order_sequence", allocationSize = 1)
    @GeneratedValue(generator = "user_order_sequence", strategy = GenerationType.SEQUENCE)
    private Long detailsId;
    private Long productId;
    private String shippingAddress;
    private String billingAddress;
    private Integer quantity;
    private Double pricePerUnit;
    private Long orderId;
}
