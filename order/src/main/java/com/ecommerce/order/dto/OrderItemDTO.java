package com.ecommerce.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderItemDTO {

    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;

}
