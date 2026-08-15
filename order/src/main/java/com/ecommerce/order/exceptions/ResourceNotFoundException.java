package com.ecommerce.order.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    String msg;

    public ResourceNotFoundException(){}

    public ResourceNotFoundException(String msg){
        super(msg);
        this.msg=msg;
    }
}
