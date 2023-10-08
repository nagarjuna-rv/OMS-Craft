package com.intuit.order.service;

import com.intuit.order.exception.CustomException;
import com.intuit.order.exception.ServerException;
import com.intuit.order.model.ProductOrderRequest;
import com.intuit.order.model.ProductResponse;
import com.intuit.order.model.ProductStockRequest;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
//import reactor.util.re

@Service
public class ProductClientService {

    public static final String API_V1_PRODUCT_ID = "http://localhost:8081/api/v1/product/{id}";
    public static final String API_V1_PRODUCT_UPDATE_STOCK = "http://localhost:8081/api/v1/product/updateStock";

    @HystrixCommand(fallbackMethod = "fallbackGetProductResponse")
    public ProductResponse getProduct(ProductOrderRequest productOrderRequest) {
        return WebClient.create().get()
                .uri(API_V1_PRODUCT_ID, productOrderRequest.getProduct().getProductId())
                .retrieve()
                .bodyToMono(ProductResponse.class)
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
//                .filter(this::is5xxServerError))
                .block();
    }

    @HystrixCommand(fallbackMethod = "fallbackUpdateProductStock")
    public void updateProductStock(ProductStockRequest productStockRequest) {
        WebClient.create().put()
                .uri(API_V1_PRODUCT_UPDATE_STOCK)
                .body(Mono.just(productStockRequest), ProductStockRequest.class)
                .retrieve()
                .bodyToMono(ProductResponse.class)
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
//                .filter(this::is5xxServerError))
        .block();
    }

    public ProductResponse fallbackGetProductResponse(ProductOrderRequest productOrderRequest) {
        throw new ServerException("Unable to fetch the given Product details"); // Example fallback response
    }

    public void fallbackUpdateProductStock(ProductStockRequest productStockRequest) {
        throw new ServerException("Unable to Update the given Product stock"); // Example fallback response

    }

    private boolean is5xxServerError(Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
    }
}

