package com.intuit.order.helper;

import com.intuit.order.entity.OrderDetail;
import com.intuit.order.entity.ProductOrder;
import com.intuit.order.enums.OrderStatus;
import com.intuit.order.model.ProductOrderRequest;
import com.intuit.order.model.ProductOrderResponse;
import com.intuit.order.model.ProductResponse;

import java.time.LocalDate;

public class ProductOrderMapper {

    public static ProductOrderResponse entityToDto(ProductOrder productOrder) {
        return ProductOrderResponse.builder()
                .productOrderId(productOrder.getOrderId())
                .quantity(productOrder.getOrderDetails().getQuantity())
                .pricePerUnit(productOrder.getOrderDetails().getPricePerUnit())
                .orderStatus(productOrder.getOrderStatus())
                .orderedOn(productOrder.getOrderedOn())
                .build();
    }

    public static ProductOrder dtoToEntity(ProductOrderRequest request, ProductResponse productResponse) {
        ProductOrder order = new ProductOrder();
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setOrderedOn(LocalDate.now());
        order.setUserId(request.getUserId());
        order.setTotalAmount(productResponse.getPrice()*request.getProduct().getQuantity());
//        OrderDetail detail = new OrderDetail();
//        detail.setQuantity(request.getProduct().getQuantity());
//        detail.setPricePerUnit(productResponse.getPrice());
//        detail.setProductId(request.getProduct().getProductId());
//        detail.setBillingAddress(request.getBillingAddress());
//        detail.setShippingAddress(request.getShippingAddress());
//        order.setOrderDetails(detail);
        return order;
    }
    public static OrderDetail dtoToOrderDetailEntity(ProductOrderRequest request, ProductResponse productResponse, Long orderId) {
        OrderDetail detail = new OrderDetail();
        detail.setQuantity(request.getProduct().getQuantity());
        detail.setPricePerUnit(productResponse.getPrice());
        detail.setProductId(request.getProduct().getProductId());
        detail.setBillingAddress(request.getBillingAddress());
        detail.setShippingAddress(request.getShippingAddress());
        detail.setOrderId(orderId);
        return detail;
    }
}
