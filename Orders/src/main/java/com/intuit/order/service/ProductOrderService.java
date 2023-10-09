package com.intuit.order.service;

import com.intuit.order.dto.*;
import com.intuit.order.entity.ProductOrder;
import com.intuit.order.enums.ActionType;
import com.intuit.order.enums.OrderStatus;
import com.intuit.order.exception.CustomException;
import com.intuit.order.mapper.ProductOrderMapper;
import com.intuit.order.repository.OrderDetailRepository;
import com.intuit.order.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
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

    public Map<Long, ProductResponse> getProducts(ProductOrderRequest productOrderRequest) {
        List<Long> productIds = productOrderRequest.getProduct().stream().map(ProductRequest::getProductId).collect(Collectors.toList());
        return productService.getProductResponseMap(productIds);
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
            List<ProductStockRequest> stockUpdates = order.getOrderDetails().stream().map(product->ProductStockRequest.builder().productId(product.getProductId()).quantity(product.getQuantity()).actionType(ActionType.COUNT_DECREMENT).build()).collect(Collectors.toList());
            productService.updateProductStock(stockUpdates);
        }

        return ProductOrderMapper.entityToDto(this.orderRepository.save(order));
    }

    @Transactional
    public ProductOrderResponse createProductOrder(ProductOrderRequest productOrderRequest, Map<Long, ProductResponse> productResponse) {
        ProductOrder productOrder = orderRepository.save(ProductOrderMapper.dtoToEntity(productOrderRequest, productResponse));
        List<ProductStockRequest> stockUpdates = productOrderRequest.getProduct().stream().map(product->ProductStockRequest.builder().productId(product.getProductId()).quantity(product.getQuantity()).actionType(ActionType.COUNT_DECREMENT).build()).collect(Collectors.toList());
        productService.updateProductStock(stockUpdates);
        return ProductOrderMapper.entityToDto(productOrder);
    }

}




/**
 * 1. Get all products(product_price)
 * 2. Get Price Quatation(productId, Quantity)
 * 3. Select Product,Qunatity and Submit Order
 *
 * **/