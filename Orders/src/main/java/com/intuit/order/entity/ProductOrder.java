package com.intuit.order.entity;

import com.intuit.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Orders")
public class ProductOrder {
    @Id
    @SequenceGenerator(name = "product_order_sequence", allocationSize = 1)
    @GeneratedValue(generator = "product_order_sequence", strategy = GenerationType.SEQUENCE)
    private Long orderId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate orderedOn;
    private String userId;
    private Double totalAmount;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private OrderDetail orderDetails;

}
