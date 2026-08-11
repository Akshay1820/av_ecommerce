package com.ecommerce.order.exceptions;

public class ProductInValidException  extends RuntimeException{
    private String message;

    public ProductInValidException() {}

    public ProductInValidException(String msg) {
        super(msg);
        this.message = msg;
    }
}
