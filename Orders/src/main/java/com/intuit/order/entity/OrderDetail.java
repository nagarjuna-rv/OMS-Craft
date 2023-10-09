package com.intuit.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderDetail {
//    @Id
//    @SequenceGenerator(name = "user_order_sequence", allocationSize = 1)
//    @GeneratedValue(generator = "user_order_sequence", strategy = GenerationType.SEQUENCE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailsId;
    private Long productId;
    private String shippingAddress;
    private String billingAddress;
    private Integer quantity;
    private Double pricePerUnit;
    @ManyToOne( fetch=FetchType.LAZY)
    @JoinColumn(name="product_order_id", referencedColumnName = "orderId", nullable = false)
    @JsonIgnoreProperties("orderDetails")
    private ProductOrder productOrder;
}
