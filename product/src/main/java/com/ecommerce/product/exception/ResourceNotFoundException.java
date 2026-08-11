package com.ecommerce.product.exception;

public class ResourceNotFoundException extends RuntimeException{

    private String message;

    public ResourceNotFoundException() {}

    public ResourceNotFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}
