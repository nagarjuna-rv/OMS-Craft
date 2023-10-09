package com.intuit.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intuit.order.enums.OrderStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ProductOrder")
public class ProductOrder {
//    @SequenceGenerator(name = "product_order_sequence", allocationSize = 1)
//    @GeneratedValue(generator = "product_order_sequence", strategy = GenerationType.SEQUENCE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate orderedOn;
    private String userId;
    private Double totalAmount;
    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JsonIgnoreProperties("productOrder")
    private List<OrderDetail> orderDetails;

}
