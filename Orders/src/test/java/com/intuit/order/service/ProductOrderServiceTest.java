package com.intuit.order.service;

import com.intuit.order.dto.*;
import com.intuit.order.entity.OrderDetail;
import com.intuit.order.entity.ProductOrder;
import com.intuit.order.enums.OrderStatus;
import com.intuit.order.exception.BadRequestException;
import com.intuit.order.exception.CustomException;
import com.intuit.order.exception.ResourceNotFoundException;
import com.intuit.order.repository.OrderDetailRepository;
import com.intuit.order.repository.ProductOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductOrderServiceTest {

    @InjectMocks
    private ProductOrderService productOrderService;

    @Mock
    private ProductOrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private ProductClientService productClientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFetchProductOrderById_ExistingOrder() {
        // Arrange
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockDbProductOrder()));

        // Act
        ProductOrderResponse result = productOrderService.fetchProductOrderById(orderId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testFetchProductOrderById_OrderNotFound() {
        // Arrange
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productOrderService.fetchProductOrderById(orderId));
    }

    @Test
    void testFetchAllProductOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(mockDbProductOrder()));

        // Act
        List<ProductOrderResponse> result = productOrderService.fetchAllProductOrders();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testUpdateProductOrderDeliveryStatus_DeliveredStatus() {
        // Arrange
        long orderId = 123L;
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(orderId, OrderStatus.DELIVERED.toString());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockDbProductOrder()));
        when(orderRepository.save(any())).thenReturn(mockDbProductOrder());
        // Act
        ProductOrderResponse result = productOrderService.updateProductOrderDeliveryStatus(updateStatusDto);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING, result
                .getOrderStatus());
    }

    @Test
    void testUpdateProductOrderDeliveryStatusWith_DeliveredStatus() {
        // Arrange
        long orderId = 123L;
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(orderId, OrderStatus.DELIVERED.toString());
        ProductOrder orderDelivered = mockDbProductOrder();
        orderDelivered.setOrderStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDelivered));

        assertThrows(CustomException.class, () -> productOrderService.updateProductOrderDeliveryStatus(updateStatusDto));
    }
    @Test
    void testUpdateProductOrderDeliveryStatusWith_CancelledStatus() {
        // Arrange
        long orderId = 123L;
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(orderId, OrderStatus.DELIVERED.toString());
        ProductOrder orderCancelled = mockDbProductOrder();
        orderCancelled.setOrderStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderCancelled));

        assertThrows(CustomException.class, () -> productOrderService.updateProductOrderDeliveryStatus(updateStatusDto));
    }

    @Test
    void testUpdateProductOrderDeliveryStatus_CancelledStatus() {
        // Arrange
        long orderId = 123L;
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(orderId, OrderStatus.CANCELLED.toString());
        ProductOrder pOrder = mockDbProductOrder();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockDbProductOrder()));
        pOrder.setOrderStatus(OrderStatus.CANCELLED);
        when(orderRepository.save(any())).thenReturn(pOrder);

        // Act
        ProductOrderResponse result = productOrderService.updateProductOrderDeliveryStatus(updateStatusDto);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getOrderStatus());
    }

    @Test
    void testUpdateProductOrderDeliveryStatus_InvalidStatus() {
        // Arrange
        long orderId = 1L;
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(orderId, "INVALID_STATUS");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockDbProductOrder()));
        when(orderRepository.save(any())).thenReturn(mockDbProductOrder());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productOrderService.updateProductOrderDeliveryStatus(updateStatusDto));
    }

    @Test
    void testCreateProductOrder() {
        // Arrange
        ProductOrderRequest productOrderRequest = new ProductOrderRequest();
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductId(1L);
        productRequest.setQuantity(5);
        productOrderRequest.setProducts(Collections.singletonList(productRequest));

        Map<Long, ProductResponse> mockProductResponse = new HashMap<>();
        mockProductResponse.put(1L, ProductResponse.builder().productId(1L).name("test").price(10.0).quantityAvailable(100).build());
        when(productClientService.getProductResponseMap(Collections.singletonList(1L))).thenReturn(mockProductResponse);
        when(orderRepository.save(any())).thenReturn(mockDbProductOrder());

        // Act
        ProductOrderResponse result = productOrderService.createProductOrder(productOrderRequest);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGetProducts() {
        // Arrange
        ProductOrderRequest productOrderRequest = new ProductOrderRequest();
        ProductRequest productRequest1 = new ProductRequest();
        productRequest1.setProductId(1L);
        productRequest1.setQuantity(3);
        ProductRequest productRequest2 = new ProductRequest();
        productRequest2.setProductId(2L);
        productRequest2.setQuantity(2);

        productOrderRequest.setProducts(Arrays.asList(productRequest1, productRequest2));

        // Mock the productClientService to return product responses
        Map<Long, ProductResponse> mockProductResponseMap = new HashMap<>();
        mockProductResponseMap.put(1L, ProductResponse.builder().productId(1L).quantityAvailable(5).build()); // Product 1 has 5 available
        mockProductResponseMap.put(2L, ProductResponse.builder().productId(2L).quantityAvailable(3).build()); // Product 2 has 3 available
        when(productClientService.getProductResponseMap(Arrays.asList(1L, 2L))).thenReturn(mockProductResponseMap);

        // Act
        Map<Long, ProductResponse> result = productOrderService.getProducts(productOrderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Both products should be in the response
        assertTrue(result.containsKey(1L)); // Product 1 should be in the response
        assertTrue(result.containsKey(2L)); // Product 2 should be in the response
        assertEquals(5, result.get(1L).getQuantityAvailable()); // Product 1 has 5 available
        assertEquals(3, result.get(2L).getQuantityAvailable()); // Product 2 has 3 available
    }

    @Test
    void testGetProducts_ProductOutOfStock() {
        // Arrange
        ProductOrderRequest productOrderRequest = new ProductOrderRequest();
        ProductRequest productRequest1 = new ProductRequest();
        productRequest1.setProductId(1L);
        productRequest1.setQuantity(5);
        productOrderRequest.setProducts(Collections.singletonList(productRequest1));

        // Mock the productClientService to return null, indicating the product is out of stock
        when(productClientService.getProductResponseMap(Collections.singletonList(1L))).thenReturn(null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productOrderService.getProducts(productOrderRequest));
    }

    @Test
    void testGetOrderableProductsMap_ProductLimitExceeded() {
        // Arrange
        ProductOrderRequest productOrderRequest = new ProductOrderRequest();
        ProductRequest productRequest1 = new ProductRequest();
        productRequest1.setProductId(1L);
        productRequest1.setQuantity(3);
        productOrderRequest.setProducts(Collections.singletonList(productRequest1));

        // Mock the productClientService to return a product response with limited availability
        Map<Long, ProductResponse> mockProductResponseMap = new HashMap<>();
        mockProductResponseMap.put(1L, ProductResponse.builder().productId(1L).quantityAvailable(2).build()); // Product 1 has 2 available
        when(productClientService.getProductResponseMap(Collections.singletonList(1L))).thenReturn(mockProductResponseMap);

        // Act
        Map<Long, ProductResponse> result = productOrderService.getOrderableProductsMap(productOrderRequest, Collections.singletonList(1L));

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size()); // Product 1 should be removed from the response due to limit exceeded
    }

    private ProductOrder mockDbProductOrder() {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setOrderId(123L);
        productOrder.setOrderStatus(OrderStatus.PROCESSING);
        productOrder.setTotalAmount(500.0);
        productOrder.setUserId("1212");

        // Create some sample OrderDetails
        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProductId(101L);
        orderDetail1.setShippingAddress("Shipping Address 1");
        orderDetail1.setBillingAddress("Billing Address 1");
        orderDetail1.setQuantity(2);
        orderDetail1.setPricePerUnit(50.0);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProductId(102L);
        orderDetail2.setShippingAddress("Shipping Address 2");
        orderDetail2.setBillingAddress("Billing Address 2");
        orderDetail2.setQuantity(3);
        orderDetail2.setPricePerUnit(75.0);

        // Add the OrderDetails to the ProductOrder
        productOrder.setOrderDetails(Arrays.asList(orderDetail1, orderDetail2));

        return productOrder;

    }
}
