package com.intuit.order.controller;

import com.intuit.order.dto.*;
import com.intuit.order.exception.BadRequestException;
import com.intuit.order.service.ProductOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product-order")
public class ProductOrderController {
    private static final Logger logger = LoggerFactory.getLogger(ProductOrderController.class);

    @Autowired
    ProductOrderService productOrderService;

    @PostMapping("/add")
    public ResponseEntity<ProductOrderResponse> createProductOrderRequest(@Valid @RequestBody ProductOrderRequest productOrderRequest) {

        Map<Long, ProductResponse> productResponse = productOrderService.getProducts(productOrderRequest);
        if (productResponse == null) {
            throw new BadRequestException("This product ran into Out of Stock. We will keep you posted once available");
        }

        for (Map.Entry<Long, ProductResponse> entry : productResponse.entrySet()) {
            Long key = entry.getKey();
            ProductResponse value = entry.getValue();
            Integer productQtyInStock = value.getQuantityAvailable();
            Optional<ProductRequest> pr = productOrderRequest.getProduct().stream().filter(productRequest -> productRequest.getProductId().equals(key)).findFirst();
            if (pr.isPresent() && productQtyInStock < pr.get().getQuantity()) {
                productResponse.remove(key);
                logger.info("Due to limited stock, Maximum Order limit for this product is {} ", productQtyInStock);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productOrderService.createProductOrder(productOrderRequest, productResponse));
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
