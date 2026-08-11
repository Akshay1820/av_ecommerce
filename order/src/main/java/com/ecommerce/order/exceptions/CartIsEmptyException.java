package com.ecommerce.order.exceptions;

public class CartIsEmptyException extends RuntimeException{
    String msg;

    public CartIsEmptyException(){}

    public CartIsEmptyException(String msg){
        super(msg);
        this.msg=msg;
    }
}
