package com.ecommerce.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CartItemResponse {

    private Long id;
    private String userName;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String userId;
}
