package com.intuit.product.controller;

import com.intuit.product.dto.ProductPriceResponse;
import com.intuit.product.dto.ProductRequest;
import com.intuit.product.dto.ProductResponse;
import com.intuit.product.dto.ProductStockRequest;
import com.intuit.product.entity.Product;
import com.intuit.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProduct();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") final Long id) {
        return productService.getProductById(id).map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/quantity/{quantity}")
    public ResponseEntity<ProductPriceResponse> getPriceQuoteByProductId(@PathVariable("id") final Long id, @PathVariable("quantity") final Integer quantity) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getPriceQuoteByProductId(id, quantity));
    }

    //System level API's
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProduct(@RequestBody ProductRequest productRequest) {
        return productService.addProduct(productRequest);
    }

    @PutMapping(value = "/update/{id}")
    public ProductResponse updateProductDetails(@PathVariable("id") final Long id, @RequestBody ProductRequest product) {
        return productService.updateProductDetails(id, product);
    }

    @PutMapping("/updateProductsStock")
    public ResponseEntity<List<Product>> updateProductStocks(@RequestBody List<ProductStockRequest> stockRequests) {
        List<Product> product = new ArrayList<>();
        for (ProductStockRequest stockRequest : stockRequests) {
            product.add(productService.updateProductStock(stockRequest));
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @DeleteMapping("/delete/{id}")
    public ProductResponse deleteProduct(@PathVariable final Long id) {
        return productService.deleteProduct(id);
    }

}
