package com.intuit.order.exception;
public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ServerException(String msg) {
        super(msg);
    }
}
