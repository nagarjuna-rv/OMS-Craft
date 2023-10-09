package com.intuit.product.service;

import com.intuit.product.dto.ProductPriceResponse;
import com.intuit.product.dto.ProductRequest;
import com.intuit.product.dto.ProductResponse;
import com.intuit.product.dto.ProductStockRequest;
import com.intuit.product.entity.Product;
import com.intuit.product.enums.ActionType;
import com.intuit.product.enums.MeasurementUnit;
import com.intuit.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddProductNewProduct() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setPrice(10.0);
        productRequest.setDescription("Description");
        productRequest.setQuantityUnit(MeasurementUnit.PIECES);
        productRequest.setQuantityAvailable(100);
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setPrice(10.0);
        newProduct.setDescription("Description");
        newProduct.setQuantityUnit(MeasurementUnit.PIECES);
        newProduct.setQuantityAvailable(100);
        when(productRepository.findByName("New Product")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(newProduct);

        Product addedProduct = productService.addProduct(productRequest);

        assertNotNull(addedProduct);
        assertEquals("New Product", addedProduct.getName());
        assertEquals(10.0, addedProduct.getPrice());
        assertEquals("Description", addedProduct.getDescription());
        assertEquals(MeasurementUnit.PIECES, addedProduct.getQuantityUnit());
        assertEquals(100, addedProduct.getQuantityAvailable());

        verify(productRepository, times(1)).findByName("New Product");
        verify(productRepository, times(1)).save(any());
    }

    @Test
    public void testAddProductExistingProduct() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Existing Product");
        productRequest.setQuantityAvailable(50);

        Product existingProduct = new Product();
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(10.0);
        existingProduct.setDescription("Original Description");
        existingProduct.setQuantityUnit(MeasurementUnit.PIECES);
        existingProduct.setQuantityAvailable(30);

        when(productRepository.findByName("Existing Product")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any())).thenReturn(existingProduct);

        Product updatedProduct = productService.addProduct(productRequest);

        assertNotNull(updatedProduct);
        assertEquals("Existing Product", updatedProduct.getName());
        assertEquals(10.0, updatedProduct.getPrice());
        assertEquals("Original Description", updatedProduct.getDescription());
        assertEquals(MeasurementUnit.PIECES, updatedProduct.getQuantityUnit());
        assertEquals(80, updatedProduct.getQuantityAvailable()); // Initial + Added quantity

        verify(productRepository, times(1)).findByName("Existing Product");
        verify(productRepository, times(1)).save(any());
    }

    @Test
    public void testGetProductById() {
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<ProductResponse> productResponse = productService.getProductById(productId);

        assertTrue(productResponse.isPresent());
        assertEquals(productId, productResponse.get().getProductId());
    }

    @Test
    public void testUpdateProductDetails() {
        Long productId = 1L;
        Product productToUpdate = new Product();
        productToUpdate.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productToUpdate));
        when(productRepository.save(any())).thenReturn(productToUpdate);

        ProductResponse updatedProductResponse = productService.updateProductDetails(productId, ProductRequest.builder().build());

        assertNotNull(updatedProductResponse);
        assertEquals(productId, updatedProductResponse.getProductId());
    }

    @Test
    public void testUpdateProductStockIncrement() {
        Long productId = 1L;
        ActionType actionType = ActionType.COUNT_INCREMENT;
        Integer quantity = 10;

        ProductStockRequest stockRequest = new ProductStockRequest(productId, quantity, actionType);

        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setQuantityAvailable(20);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any())).thenReturn(existingProduct);

        Product updatedProduct = productService.updateProductStock(stockRequest);

        assertNotNull(updatedProduct);
        assertEquals(productId, updatedProduct.getProductId());
        assertEquals(30, updatedProduct.getQuantityAvailable()); // Initial quantity + added quantity

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateProductStockDecrement() {
        Long productId = 1L;
        ActionType actionType = ActionType.COUNT_DECREMENT;
        Integer quantity = 5;

        ProductStockRequest stockRequest = new ProductStockRequest();
        stockRequest.setProductId(productId);
        stockRequest.setActionType(actionType);
        stockRequest.setQuantity(quantity);

        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setQuantityAvailable(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any())).thenReturn(existingProduct);

        Product updatedProduct = productService.updateProductStock(stockRequest);

        assertNotNull(updatedProduct);
        assertEquals(productId, updatedProduct.getProductId());
        assertEquals(5, updatedProduct.getQuantityAvailable()); // Initial quantity - deducted quantity

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateProductStockProductNotFound() {
        Long productId = 1L;
        ActionType actionType = ActionType.COUNT_INCREMENT;
        Integer quantity = 10;

        ProductStockRequest stockRequest = new ProductStockRequest();
        stockRequest.setProductId(productId);
        stockRequest.setActionType(actionType);
        stockRequest.setQuantity(quantity);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProductStock(stockRequest));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testDeleteProductExisting() {
        Long productId = 1L;
        Product productToDelete = new Product();
        productToDelete.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productToDelete));

        ProductResponse response = productService.deleteProduct(productId);

        assertNotNull(response);
        assertEquals(productId, response.getProductId());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testDeleteProductNonExisting() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(Product.builder().productId(1L).build()));

        ProductResponse response = productService.deleteProduct(productId);

        assertNotNull(response);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetPriceQuoteByProductId() {
        Long productId = 1L;
        Integer quantity = 5;
        double pricePerUnit = 10.0;

        Product product = new Product();
        product.setProductId(productId);
        product.setPrice(pricePerUnit);
        product.setQuantityAvailable(20);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductPriceResponse response = productService.getPriceQuoteByProductId(productId, quantity);

        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals(pricePerUnit, response.getPricePerUnit());
        assertEquals(quantity, response.getQuantity());
        assertEquals(pricePerUnit * quantity, response.getTotalPrice());
    }

    @Test
    void testGetPriceQuoteByProductIdNotFound() {
        Long productId = 1L;
        Integer quantity = 5;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getPriceQuoteByProductId(productId, quantity));
    }

    @Test
    void testGetPriceQuoteByProductIdOutOfStock() {
        Long productId = 1L;
        Integer quantity = 25;

        Product product = new Product();
        product.setProductId(productId);
        product.setQuantityAvailable(20);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.getPriceQuoteByProductId(productId, quantity));
    }

    @Test
    public void testGetAllProduct() {

        List<Product> productList = new ArrayList<>();
        productList.add(new Product());
        productList.add(new Product());

        when(productRepository.findAll()).thenReturn(productList);

        List<ProductResponse> responseList = productService.getAllProduct();

        assertNotNull(responseList);
        assertEquals(2, responseList.size());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllProductEmptyList() {
        List<Product> productList = new ArrayList<>();

        when(productRepository.findAll()).thenReturn(productList);

        List<ProductResponse> responseList = productService.getAllProduct();

        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());

        verify(productRepository, times(1)).findAll();
    }
}
