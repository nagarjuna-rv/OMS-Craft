package com.intuit.product.service;

import com.intuit.product.dto.ProductPriceResponse;
import com.intuit.product.dto.ProductRequest;
import com.intuit.product.dto.ProductResponse;
import com.intuit.product.dto.ProductStockRequest;
import com.intuit.product.entity.Product;
import com.intuit.product.enums.ActionType;
import com.intuit.product.exception.BadRequestException;
import com.intuit.product.exception.ResourceNotFoundException;
import com.intuit.product.mapper.ProductResponseMapper;
import com.intuit.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intuit.product.constant.Constants.OUT_OF_STOCK;
import static com.intuit.product.constant.Constants.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product addProduct(ProductRequest productRequest) {
        Optional<Product> fetchedProduct = productRepository.findByName(productRequest.getName());
        if (fetchedProduct.isPresent()) {
            fetchedProduct.get().setQuantityAvailable(fetchedProduct.get().getQuantityAvailable() + productRequest.getQuantityAvailable());
            return productRepository.save(fetchedProduct.get());
        }

        return productRepository.save(Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .quantityUnit(productRequest.getQuantityUnit())
                .quantityAvailable(productRequest.getQuantityAvailable())
                .build());
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id).map(ProductResponseMapper::mapProductEntityToProductResponse);
    }

    public ProductPriceResponse getPriceQuoteByProductId(Long id, Integer quantity) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND + id));
        if (product.getQuantityAvailable() < quantity) {
            throw new BadRequestException(OUT_OF_STOCK + product.getQuantityAvailable());
        }
        return ProductPriceResponse.builder().productId(product.getProductId())
                .pricePerUnit(product.getPrice())
                .quantity(quantity)
                .totalPrice(product.getPrice() * quantity).build();
    }

    /**
     * method used to update product Details
     */
    @Override
    public ProductResponse updateProductDetails(Long productId, ProductRequest product) {
        Product dbProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND + productId));
        Product savedProduct = saveUpdatedProduct(product, dbProduct);
        return ProductResponseMapper.mapProductEntityToProductResponse(savedProduct);
    }

    @Override
    public Product updateProductStock(ProductStockRequest stock) {
        Product fetchedProduct = productRepository.findById(stock.getProductId()).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND + stock.getProductId()));
        Integer currentProductQuantity = fetchedProduct.getQuantityAvailable();

        if (StringUtils.equals(ActionType.COUNT_INCREMENT.name(), stock.getActionType().name())) {
            fetchedProduct.setQuantityAvailable(currentProductQuantity + stock.getQuantity());
        } else {
            fetchedProduct.setQuantityAvailable(currentProductQuantity - stock.getQuantity());
        }
        return productRepository.save(fetchedProduct);
    }

    /**
     * method used to delete Product
     */
    @Override
    public ProductResponse deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND + productId));
        return ProductResponseMapper.mapProductEntityToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(ProductResponseMapper::mapProductEntityToProductResponse).collect(Collectors.toList());
    }

    private Product saveUpdatedProduct(ProductRequest product, Product dbProduct) {
        if (product.getDescription() != null) dbProduct.setDescription(product.getDescription());
        if (product.getPrice() != null) dbProduct.setPrice(product.getPrice());
        if (product.getName() != null) dbProduct.setName(product.getName());
        if (product.getQuantityAvailable() != null) dbProduct.setQuantityAvailable(product.getQuantityAvailable());
        if (product.getQuantityUnit() != null) dbProduct.setQuantityUnit(product.getQuantityUnit());
        return productRepository.save(dbProduct);
    }
}
