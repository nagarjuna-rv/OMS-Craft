package com.intuit.order.mapper;

import com.intuit.order.dto.*;
import com.intuit.order.entity.OrderDetail;
import com.intuit.order.entity.ProductOrder;
import com.intuit.order.enums.OrderStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductOrderMapper {

    public static ProductOrderResponse entityToDto(ProductOrder productOrder) {
        return ProductOrderResponse.builder()
                .orderId(productOrder.getOrderId())
                .products(productOrder.getOrderDetails().stream().map(order -> OrderProduct.builder()
                        .productId(order.getProductId())
                        .shippingAddress(order.getShippingAddress())
                        .billingAddress(order.getBillingAddress())
                        .quantity(order.getQuantity())
                        .pricePerUnit(order.getPricePerUnit()).build()).collect(Collectors.toList()))
                .orderStatus(productOrder.getOrderStatus())
                .totalAmount(productOrder.getTotalAmount())
                .userId(productOrder.getUserId())
                .build();
    }

    public static ProductOrder dtoToEntity(ProductOrderRequest request, Map<Long, ProductResponse> productResponse) {
        ProductOrder order = new ProductOrder();
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setOrderedOn(LocalDate.now());
        order.setUserId(request.getUserId());
        double totalAmount = 0.0;
        for (ProductRequest req : request.getProducts()) {
            totalAmount += (productResponse.get(req.getProductId()).getPrice()) * req.getQuantity();
        }
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(new ArrayList<>());
        order.getOrderDetails().addAll(mapToOrderDetailListEntity(request, productResponse, order));
        return order;
    }

    public static List<OrderDetail> mapToOrderDetailListEntity(ProductOrderRequest request, Map<Long, ProductResponse> productResponse, ProductOrder order) {
        List<OrderDetail> details = new ArrayList<>();
        for (ProductRequest req : request.getProducts()) {
            OrderDetail detail = new OrderDetail();
            detail.setQuantity(req.getQuantity());
            detail.setPricePerUnit(productResponse.get(req.getProductId()).getPrice());
            detail.setProductId(req.getProductId());
            detail.setBillingAddress(request.getBillingAddress());
            detail.setShippingAddress(request.getShippingAddress());
            detail.setProductOrder(order);
            details.add(detail);
        }

        return details;
    }
}
