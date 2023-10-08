package com.intuit.product.service;

import com.intuit.product.dto.ProductPriceResponse;
import com.intuit.product.dto.ProductRequest;
import com.intuit.product.dto.ProductResponse;
import com.intuit.product.dto.ProductStockRequest;
import com.intuit.product.entity.Product;
import com.intuit.product.enums.ActionType;
import com.intuit.product.mapper.ProductResponseMapper;
import com.intuit.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public Product addProduct(ProductRequest productRequest) {
        Optional<Product> fetchedProduct = productRepository.findByName(productRequest.getName());
        if(fetchedProduct.isPresent()) {
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

        Optional<Product> optionalProduct =  productRepository.findById(id);
        if(optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if(product.getQuantityAvailable() < quantity) {
                throw new RuntimeException("Due to limited stock, Maximum Order limit for this product is "+ product.getQuantityAvailable());
            }
            return ProductPriceResponse.builder().productId(product.getProductId())
                    .pricePerUnit(product.getPrice())
                    .quantity(quantity)
                    .totalPrice(product.getPrice() * quantity).build();
        }
        else {
            throw new RuntimeException("No Product found for given id "+ id);
        }
       }

    /**
     * method used to update product Details
     */
    @Override
    public ProductResponse updateProductDetails(Long productId ,Product product) {
        productRepository.findById(productId).ifPresent(prod -> {
            if(product.getDescription() !=null)prod.setDescription(product.getDescription());
            if(product.getPrice() !=null)prod.setPrice(product.getPrice());
            if(product.getName() !=null)prod.setName(product.getName());
            if(product.getQuantityAvailable() !=null)prod.setQuantityAvailable(product.getQuantityAvailable());
            if(product.getQuantityUnit() !=null)prod.setQuantityUnit(prod.getQuantityUnit());
            productRepository.save(prod);
        });
        product.setProductId(productId);
        return ProductResponseMapper.mapProductEntityToProductResponse(product);
    }
    @Override
    public Product updateProductStock(ProductStockRequest stock) {
        Product fetchedProduct = productRepository.findById(stock.getProductId()).orElseThrow(() -> new RuntimeException());
        Integer currentProductQuantity = fetchedProduct.getQuantityAvailable();

        if(StringUtils.equals(ActionType.COUNT_INCREMENT.name(), stock.getActionType().name())) {
            fetchedProduct.setQuantityAvailable(currentProductQuantity+stock.getQuantity());
        } else {
            fetchedProduct.setQuantityAvailable(currentProductQuantity-stock.getQuantity());
        }
        return productRepository.save(fetchedProduct);
    }
    /**
     * method used to delete Product
     */
    @Override
    public ProductResponse deleteProduct(Long productId) {
        final Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty()) {
            return null;
        }
        return ProductResponseMapper.mapProductEntityToProductResponse(product.get());
    }

    @Override
    public List<ProductResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(ProductResponseMapper::mapProductEntityToProductResponse).collect(Collectors.toList());
    }
}
