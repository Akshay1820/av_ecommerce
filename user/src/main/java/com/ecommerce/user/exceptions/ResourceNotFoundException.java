package com.ecommerce.user.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    private String message;

    public ResourceNotFoundException() {}

    public ResourceNotFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}
