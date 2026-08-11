package com.ecommerce.order;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.exceptions.ProductInValidException;
import com.ecommerce.order.exceptions.ProductOutOfStockException;
import com.ecommerce.order.service.ExternalAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final ExternalAPIService apiService;

    public UserResponse validateUser(String userId) {
        UserResponse userResponse = apiService.getUser(userId);
        return userResponse;
    }

    public void validateProduct(CartItemRequest request, ProductResponse productResponse) {
        if (Objects.nonNull(productResponse.getActive()) &&
                Boolean.FALSE.equals(productResponse.getActive())) {
            throw new ProductInValidException("Product " + request.getProductId() + " is invalid");
        }

        int availableStockQuantity = productResponse.getStockQuantity();
        int requestedStockQuantity = request.getQuantity();
        if (availableStockQuantity < requestedStockQuantity) {
            throw new ProductOutOfStockException("Product " + request.getProductId() + " is out of stock");
        }
    }
}
