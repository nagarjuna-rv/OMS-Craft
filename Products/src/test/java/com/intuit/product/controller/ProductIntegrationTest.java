package com.intuit.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.product.dto.ProductPriceResponse;
import com.intuit.product.dto.ProductRequest;
import com.intuit.product.dto.ProductResponse;
import com.intuit.product.dto.ProductStockRequest;
import com.intuit.product.entity.Product;
import com.intuit.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponse mockProductResponse;
    private ProductRequest mockProductRequest;

    @BeforeEach
    public void setUp() {
        mockProductResponse = new ProductResponse();
        mockProductResponse.setProductId(1L);
        mockProductResponse.setName("Test Product");
        mockProductResponse.setPrice(10.0);

        mockProductRequest = new ProductRequest();
        mockProductRequest.setName("Test Product");
        mockProductRequest.setPrice(10.0);
    }

    @Test
    public void testAddProduct() throws Exception {

        when(productService.addProduct(any(ProductRequest.class))).thenReturn(new Product());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProductRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testGetAllProducts() throws Exception {

        when(productService.getAllProduct()).thenReturn(Collections.singletonList(mockProductResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(10.0));
    }

    @Test
    public void testGetProductById() throws Exception {

        when(productService.getProductById(1L)).thenReturn(Optional.of(mockProductResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.0));
    }

    @Test
    public void testUpdateProductDetails() throws Exception {

        when(productService.updateProductDetails(1L, new ProductRequest())).thenReturn(mockProductResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Product())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.0));
    }

    @Test
    public void testUpdateProductStock() throws Exception {
        when(productService.updateProductStock(any(ProductStockRequest.class))).thenReturn(new Product());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/updateProductsStock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(new ProductStockRequest()))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(mockProductResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.0));
    }

    @Test
    public void testGetPriceQuoteByProductId() throws Exception {
        long productId = 1L;
        int quantity = 5;

        ProductPriceResponse productPriceResponse = new ProductPriceResponse();
        productPriceResponse.setProductId(productId);
        productPriceResponse.setQuantity(quantity);
        productPriceResponse.setTotalPrice(50.0);

        when(productService.getPriceQuoteByProductId(productId, quantity)).thenReturn(productPriceResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/products/{id}/quantity/{quantity}", productId, quantity)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(quantity))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPrice").value(50.0))
                .andReturn();
    }
}
