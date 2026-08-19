package com.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotBlank(message = "product id should not be blank")
    private String productId;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a strictly positive number")
    private Integer quantity;
}
