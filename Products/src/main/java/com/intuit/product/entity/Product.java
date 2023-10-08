package com.intuit.product.entity;

import com.intuit.product.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "Products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Product {
    @Id
    @SequenceGenerator(name = "product_id_sequence", initialValue = 100000, allocationSize = 1)
    @GeneratedValue(generator = "product_id_sequence", strategy = GenerationType.SEQUENCE)
    private Long productId;
    @Column(length = 20, unique = true)
    private String name;
    @Column(length = 100)
    private String description;
    private Integer quantityAvailable;
    private Double price;
    @Enumerated(EnumType.STRING)
    private MeasurementUnit quantityUnit;

}
