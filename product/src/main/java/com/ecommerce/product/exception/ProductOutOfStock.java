package com.ecommerce.product.exception;

public class ProductOutOfStock extends RuntimeException{

    private String message;

    public ProductOutOfStock() {}

    public ProductOutOfStock(String msg) {
        super(msg);
        this.message = msg;
    }
}
