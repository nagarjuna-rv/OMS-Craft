package com.intuit.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intuit.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ProductOrder")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate orderedOn;
    private String userId;
    private Double totalAmount;
    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("productOrder")
    private List<OrderDetail> orderDetails;

}
