package com.ecommerce.order;

import com.ecommerce.order.dto.ErrorResponse;
import com.ecommerce.order.exceptions.ProductInValidException;
import com.ecommerce.order.exceptions.ProductNotAvailableException;
import com.ecommerce.order.exceptions.ProductOutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductNotAvailableException ex, WebRequest req) {
        ErrorResponse err = new ErrorResponse(404, "Not Found", ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductOutOfStockException ex, WebRequest req) {
        ErrorResponse err = new ErrorResponse(409, "Not Found", ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductInValidException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductInValidException ex, WebRequest req) {
        ErrorResponse err = new ErrorResponse(409, "Not Found", ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }
}
