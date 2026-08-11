package com.ecommerce.product;

import com.ecommerce.product.dto.ErrorResponse;
import com.ecommerce.product.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        ErrorResponse err = new ErrorResponse(404, "Not Found", ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }
}
