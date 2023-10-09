package com.intuit.order.service;

import com.intuit.order.dto.ProductResponse;
import com.intuit.order.dto.ProductStockRequest;
import com.intuit.order.exception.CustomException;
import com.intuit.order.exception.ServerException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static com.intuit.order.constant.Constants.API_V1_PRODUCT_ID;
import static com.intuit.order.constant.Constants.API_V1_PRODUCT_UPDATE_STOCK;

@Service
public class ProductClientService {
    private static final Logger logger = LoggerFactory.getLogger(ProductClientService.class);

    @HystrixCommand(fallbackMethod = "fallbackGetProductResponse")
    public Map<Long, ProductResponse> getProductResponseMap(List<Long> productIds) {
        Mono<Map<Long, ProductResponse>> resp = Flux.fromIterable(productIds)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(productId -> getProduct(productId).map(product -> new AbstractMap.SimpleEntry<>(productId, product))).sequential()
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
        return resp.block();
    }

    @HystrixCommand(fallbackMethod = "fallbackUpdateProductStock")
    public void updateProductStock(List<ProductStockRequest> productStockRequest) {
        WebClient.create().put()
                .uri(API_V1_PRODUCT_UPDATE_STOCK)
                .body(Mono.just(productStockRequest), new ParameterizedTypeReference<List<ProductStockRequest>>() {
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductResponse>>() {
                })
                .block();
    }

    public Map<Long, ProductResponse> fallbackGetProductResponse(List<Long> productOrderRequest, Throwable t) {
        logger.error("From Hystrix fallback fallbackGetProductResponse : ", t);
        if (is5xxServerError(t)) {
            throw new ServerException("Unable to fetch the given Product details");
        } else {
            throw new CustomException("Unable to fetch the given Product details");
        }
    }

    public void fallbackUpdateProductStock(List<ProductStockRequest> productStockRequest, Throwable t) {
        logger.error("From Hystrix fallback fallbackUpdateProductStock : ", t);
        throw new ServerException("Unable to Update the given Product stock"); // Example fallback response

    }

    private Mono<ProductResponse> getProduct(Long productId) {
        return WebClient.create().get()
                .uri(API_V1_PRODUCT_ID, productId)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }

    private boolean is5xxServerError(Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
    }
}

