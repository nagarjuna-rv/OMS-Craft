package com.intuit.order.service;

import com.intuit.order.entity.OrderDetail;
import com.intuit.order.entity.ProductOrder;
import com.intuit.order.enums.ActionType;
import com.intuit.order.enums.OrderStatus;
import com.intuit.order.exception.CustomException;
import com.intuit.order.helper.ProductOrderMapper;
import com.intuit.order.model.*;
import com.intuit.order.repository.OrderDetailRepository;
import com.intuit.order.repository.ProductOrderRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductOrderService {

    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductClientService productService;

    public ProductOrderResponse fetchProductOrderById(long productOrderId) {
        return ProductOrderMapper.entityToDto(this.orderRepository.findById(productOrderId)
                .orElseThrow(() -> new RuntimeException("productOrderId Not found")));
    }

    public List<ProductOrderResponse> fetchAllProductOrders() {
        return orderRepository.findAll().stream().map(ProductOrderMapper::entityToDto).collect(Collectors.toList());
    }

    @Transactional
    public ProductOrderResponse updateProductOrderDeliveryStatus(UpdateStatusDto updateStatusDto) {
        ProductOrder order = orderRepository.findById(updateStatusDto.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Product Order Id Not found"));

        if (order.getOrderStatus().equals(OrderStatus.DELIVERED))
            throw new CustomException("Product already delivered");

        if (order.getOrderStatus().equals(OrderStatus.CANCELLED))
            throw new CustomException("Product delivery was already cancelled");

        if (updateStatusDto.getStatus().equals(OrderStatus.DELIVERED.toString()))
            order.setOrderStatus(OrderStatus.valueOf(updateStatusDto.getStatus()));
        if (updateStatusDto.getStatus().equals(OrderStatus.CANCELLED.toString())) {
            order.setOrderStatus(OrderStatus.valueOf(updateStatusDto.getStatus()));
            ProductStockRequest productStockRequest = ProductStockRequest.builder().productId(order.getOrderDetails().getProductId()).quantity(order.getOrderDetails().getQuantity()).actionType(ActionType.COUNT_INCREMENT).build();
            productService.updateProductStock(productStockRequest);
        }

        return ProductOrderMapper.entityToDto(this.orderRepository.save(order));
    }

    @Transactional
    public ProductOrderResponse createProductOrder(ProductOrderRequest productOrderRequest, ProductResponse productResponse) {
        ProductOrder productOrder = orderRepository.save(ProductOrderMapper.dtoToEntity(productOrderRequest, productResponse));
        OrderDetail orderDetail = orderDetailRepository.save(ProductOrderMapper.dtoToOrderDetailEntity(productOrderRequest, productResponse, productOrder.getOrderId()));
        productOrder.setOrderDetails(orderDetail);
        ProductStockRequest productStockRequest = ProductStockRequest.builder().productId(productOrderRequest.getProduct().getProductId()).quantity(productOrderRequest.getProduct().getQuantity()).actionType(ActionType.COUNT_DECREMENT).build();
        productService.updateProductStock(productStockRequest);
        return ProductOrderMapper.entityToDto(productOrder);
    }

}




/**
 * 1. Get all products(product_price)
 * 2. Get Price Quatation(productId, Quantity)
 * 3. Select Product,Qunatity and Submit Order
 *
 * **/