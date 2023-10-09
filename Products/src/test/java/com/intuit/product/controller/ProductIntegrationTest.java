package com.intuit.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        // Initialize mock data
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
        // Mock the productService to return the mockProductResponse
        when(productService.addProduct(any(ProductRequest.class))).thenReturn(new Product());

        // Perform the POST request to add a product
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProductRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Mock the productService to return a list of mockProductResponse
        when(productService.getAllProduct()).thenReturn(Collections.singletonList(mockProductResponse));

        // Perform the GET request to get all products
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(10.0));
    }

    @Test
    public void testGetProductById() throws Exception {
        // Mock the productService to return an optional of mockProductResponse
        when(productService.getProductById(1L)).thenReturn(Optional.of(mockProductResponse));

        // Perform the GET request to get a product by ID
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.0));
    }

    @Test
    public void testUpdateProductDetails() throws Exception {
        // Mock the productService to return a mockProductResponse
        when(productService.updateProductDetails(1L, new ProductRequest())).thenReturn(mockProductResponse);

        // Perform the PUT request to update a product's details
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Product())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.0));
    }

    @Test
    public void testUpdateProductStock() throws Exception {
        // Mock the productService to return a mock Product
        when(productService.updateProductStock(any(ProductStockRequest.class))).thenReturn(new Product());

        // Perform the PUT request to update a product's stock
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product/updateStock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductStockRequest())))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Mock the productService to return a mockProductResponse
        when(productService.deleteProduct(1L)).thenReturn(mockProductResponse);

        // Perform the DELETE request to delete a product by ID
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/product/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10.0));
    }
}
