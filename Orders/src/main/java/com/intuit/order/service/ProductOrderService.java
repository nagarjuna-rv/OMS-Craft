package com.intuit.order.service;

import com.intuit.order.dto.*;
import com.intuit.order.entity.ProductOrder;
import com.intuit.order.enums.ActionType;
import com.intuit.order.enums.OrderStatus;
import com.intuit.order.exception.BadRequestException;
import com.intuit.order.exception.CustomException;
import com.intuit.order.exception.ResourceNotFoundException;
import com.intuit.order.mapper.ProductOrderMapper;
import com.intuit.order.repository.OrderDetailRepository;
import com.intuit.order.repository.ProductOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intuit.order.constant.Constants.*;

@Service
public class ProductOrderService {
    private static final Logger logger = LoggerFactory.getLogger(ProductOrderService.class);
    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductClientService productClientService;

    public ProductOrderResponse fetchProductOrderById(long productOrderId) {
        return ProductOrderMapper.entityToDto(orderRepository.findById(productOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("productOrderId Not found")));
    }

    public List<ProductOrderResponse> fetchAllProductOrders() {
        return orderRepository.findAll().stream().map(ProductOrderMapper::entityToDto).collect(Collectors.toList());
    }

    public Map<Long, ProductResponse> getProducts(ProductOrderRequest productOrderRequest) {
        List<Long> productIds = productOrderRequest.getProducts().stream().map(ProductRequest::getProductId).collect(Collectors.toList());
        return getOrderableProductsMap(productOrderRequest, productIds);
    }

    @Transactional
    public ProductOrderResponse updateProductOrderDeliveryStatus(UpdateStatusDto updateStatusDto) {
        ProductOrder order = orderRepository.findById(updateStatusDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_ORDER_NOT_FOUND));
        validateOrderStatus(order);
        if (updateStatusDto.getStatus().equals(OrderStatus.DELIVERED.toString()))
            order.setOrderStatus(OrderStatus.valueOf(updateStatusDto.getStatus()));
        else if (updateStatusDto.getStatus().equals(OrderStatus.CANCELLED.toString())) {
            order.setOrderStatus(OrderStatus.valueOf(updateStatusDto.getStatus()));
            List<ProductStockRequest> stockUpdates = order.getOrderDetails().stream().map(product -> ProductStockRequest.builder().productId(product.getProductId()).quantity(product.getQuantity()).actionType(ActionType.COUNT_INCREMENT).build()).collect(Collectors.toList());
            productClientService.updateProductStock(stockUpdates);
        }
        else {
            throw new BadRequestException("Invalid Status Provided");
        }
        return ProductOrderMapper.entityToDto(orderRepository.save(order));
    }

    @Transactional
    public ProductOrderResponse createProductOrder(ProductOrderRequest productOrderRequest) {
        Map<Long, ProductResponse> productResponse = getProducts(productOrderRequest);
        ProductOrder productOrder = orderRepository.save(ProductOrderMapper.dtoToEntity(productOrderRequest, productResponse));
        List<ProductStockRequest> stockUpdates = productOrderRequest.getProducts().stream().map(product -> ProductStockRequest.builder().productId(product.getProductId()).quantity(product.getQuantity()).actionType(ActionType.COUNT_DECREMENT).build()).collect(Collectors.toList());
        productClientService.updateProductStock(stockUpdates);
        return ProductOrderMapper.entityToDto(productOrder);
    }

    Map<Long, ProductResponse> getOrderableProductsMap(ProductOrderRequest productOrderRequest, List<Long> productIds) {
        Map<Long, ProductResponse> productResponse = productClientService.getProductResponseMap(productIds);
        if (productResponse == null) {
            throw new BadRequestException(PRODUCT_OUT_OF_STOCK);
        }

        for (Map.Entry<Long, ProductResponse> entry : productResponse.entrySet()) {
            Long key = entry.getKey();
            ProductResponse value = entry.getValue();
            Integer productQtyInStock = value.getQuantityAvailable();
            Optional<ProductRequest> pr = productOrderRequest.getProducts().stream().filter(productRequest -> productRequest.getProductId().equals(key)).findFirst();
            if (pr.isPresent() && productQtyInStock < pr.get().getQuantity()) {
                productResponse.remove(key);
                logger.info(PRODUCT_LIMIT_EXCEEDED, productQtyInStock);
            }
        }
        return productResponse;
    }

    private void validateOrderStatus(ProductOrder order) {
        if (order.getOrderStatus().equals(OrderStatus.DELIVERED))
            throw new CustomException("Product already delivered");
        if (order.getOrderStatus().equals(OrderStatus.CANCELLED))
            throw new CustomException("Product delivery was already cancelled");
    }

}