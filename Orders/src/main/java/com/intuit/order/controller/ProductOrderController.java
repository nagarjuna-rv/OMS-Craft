package com.intuit.order.controller;

import com.intuit.order.dto.ProductOrderRequest;
import com.intuit.order.dto.ProductOrderResponse;
import com.intuit.order.dto.UpdateStatusDto;
import com.intuit.order.service.ProductOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product-order")
public class ProductOrderController {
    private static final Logger logger = LoggerFactory.getLogger(ProductOrderController.class);

    @Autowired
    ProductOrderService productOrderService;

    @PostMapping("/add")
    public ResponseEntity<ProductOrderResponse> createProductOrderRequest(@Valid @RequestBody ProductOrderRequest productOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productOrderService.createProductOrder(productOrderRequest));
    }

    @PutMapping
    public ResponseEntity<ProductOrderResponse> updateDeliveryStatus(@Valid @RequestBody UpdateStatusDto updateStatusDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.productOrderService.updateProductOrderDeliveryStatus(updateStatusDto));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ProductOrderResponse> getOrderById(@PathVariable long orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.productOrderService.fetchProductOrderById(orderId));
    }

    //System Level APIs
    @GetMapping("/all")
    public ResponseEntity<List<ProductOrderResponse>> getAllProductOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(this.productOrderService.fetchAllProductOrders());
    }
}
