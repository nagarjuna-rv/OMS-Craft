package com.intuit.order.controller;

import com.intuit.order.exception.BadRequestException;
import com.intuit.order.model.ProductOrderRequest;
import com.intuit.order.model.ProductOrderResponse;
import com.intuit.order.model.ProductResponse;
import com.intuit.order.model.UpdateStatusDto;
import com.intuit.order.service.ProductClientService;
import com.intuit.order.service.ProductOrderService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-order")
public class ProductOrderController {

    @Autowired
    ProductOrderService productOrderService;

    @Autowired
    ProductClientService productService;

    @PostMapping("/add")
    public ResponseEntity<?> createProductOrderRequest(@Valid @RequestBody ProductOrderRequest productOrderRequest) {

        ProductResponse productResponse = productService.getProduct(productOrderRequest);
        if (productResponse == null) {
            throw new BadRequestException("This product ran into Out of Stock. We will keep you posted once available");
        }
        Integer productQtyInStock = productResponse.getQuantityAvailable();
        if (productQtyInStock < productOrderRequest.getProduct().getQuantity()) {
            throw new BadRequestException("Due to limited stock, Maximum Order limit for this product is " + productQtyInStock);
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
    @GetMapping
    public ResponseEntity<List<ProductOrderResponse>> fetchAllProductOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(this.productOrderService.fetchAllProductOrders());
    }
}
