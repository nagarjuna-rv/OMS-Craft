package com.intuit.order.constant;

public class Constants {
    public static final String API_V1_PRODUCT_ID = "http://localhost:8081/api/v1/products/{id}";
    public static final String API_V1_PRODUCT_UPDATE_STOCK = "http://localhost:8081/api/v1/products/updateProductsStock";
    public static final String EXCEPTION_MESSAGE = "Exception {}";
    public static final String PRODUCT_OUT_OF_STOCK = "This product ran into Out of Stock. We will keep you posted once available";
    public static final String PRODUCT_LIMIT_EXCEEDED = "Due to limited stock, Maximum Order limit for this product is {} ";
    public static final String PRODUCT_ORDER_NOT_FOUND = "Product Order Id Not found";
}
