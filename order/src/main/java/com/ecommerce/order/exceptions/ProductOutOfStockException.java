package com.ecommerce.order.exceptions;

public class ProductOutOfStockException extends RuntimeException{

    private String message;

    ProductOutOfStockException(){}

    public ProductOutOfStockException(String msg){
        super(msg);
        this.message=msg;
    }
}
