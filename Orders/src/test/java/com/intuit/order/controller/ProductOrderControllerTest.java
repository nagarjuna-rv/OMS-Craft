package com.intuit.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.order.dto.ProductOrderRequest;
import com.intuit.order.dto.ProductOrderResponse;
import com.intuit.order.dto.UpdateStatusDto;
import com.intuit.order.service.ProductOrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductOrderService productOrderService;


    @Test
    void testCreateProductOrderRequest() throws Exception {
        ProductOrderRequest request = new ProductOrderRequest(); // Create a valid request object
        request.setUserId("testuser");
        request.setProducts(new ArrayList<>());
        Mockito.when(productOrderService.createProductOrder(Mockito.any(ProductOrderRequest.class))).thenReturn(new ProductOrderResponse());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/product-order/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    void testUpdateDeliveryStatus() throws Exception {
        UpdateStatusDto updateStatusDto = new UpdateStatusDto();
        updateStatusDto.setStatus("DELIVERED");
        updateStatusDto.setOrderId(1L);
        Mockito.when(productOrderService.updateProductOrderDeliveryStatus(Mockito.any(UpdateStatusDto.class))).thenReturn(new ProductOrderResponse());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/product-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusDto)))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void testGetOrderById() throws Exception {
        long orderId = 1L;

        Mockito.when(productOrderService.fetchProductOrderById(orderId)).thenReturn(new ProductOrderResponse());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/product-order/{orderId}", orderId))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testGetAllProductOrders() throws Exception {
        Mockito.when(productOrderService.fetchAllProductOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/product-order/all"))
                .andExpect(status().isOk())
                .andReturn();

    }
}
