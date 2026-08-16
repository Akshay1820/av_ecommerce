package com.ecommerce.order;


import com.ecommerce.order.UtilService;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exceptions.ProductOutOfStockException;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import com.ecommerce.order.service.CartItemService;
import com.ecommerce.order.service.ExternalAPIService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ExternalAPIService apiService;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private CartItemService cartItemService;

    @Test
    void shouldRejectAddToCartWhenFinalQuantityExceedsStock() {
        // Given: stock = 10 and the cart already contains 7
        String userId = "user-1";
        String productId = "1";

        CartItem existingCartItem = new CartItem();
        existingCartItem.setProductId(productId);
        existingCartItem.setUserId(userId);
        existingCartItem.setQuantity(7);
        existingCartItem.setPrice(BigDecimal.valueOf(700));

        CartItemRequest request = new CartItemRequest();
        request.setProductId(productId);
        request.setQuantity(5);

        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setActive(true);
        product.setStockQuantity(10);
        product.setPrice(BigDecimal.valueOf(100));

        when(apiService.getProductData(1L)).thenReturn(product);
        when(cartItemRepository.findAllByUserId(userId))
                .thenReturn(List.of(existingCartItem));

        // When + Then: 7 + 5 = 12, which is greater than stock 10
        assertThrows(
                ProductOutOfStockException.class,
                () -> cartItemService.addToCart(userId, request)
        );

        // Most important assertion: no cart change was saved
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void shouldAddToExistingCartWhenFinalQuantityEqualsAvailableStock() {
        // Given: stock = 10, existing cart quantity = 7, requested quantity = 3
        String userId = "user-1";
        String productId = "1";
        CartItem existingCartItem = cartItem(userId, productId, 7);
        CartItemRequest request = request(productId, 3);

        when(apiService.getProductData(1L)).thenReturn(activeProductWithStock(10));
        when(cartItemRepository.findAllByUserId(userId)).thenReturn(List.of(existingCartItem));
        when(cartItemRepository.findByProductIdAndUserId(productId, userId))
                .thenReturn(Optional.of(existingCartItem));

        // When
        cartItemService.addToCart(userId, request);

        // Then: 7 + 3 = 10, so the item is saved at the stock boundary
        assertEquals(10, existingCartItem.getQuantity());
        assertEquals(BigDecimal.valueOf(1000), existingCartItem.getPrice());
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    void shouldAddNewCartItemWhenRequestedQuantityIsWithinStock() {
        // Given: stock = 10, no existing cart item, requested quantity = 5
        String userId = "user-1";
        String productId = "1";
        CartItemRequest request = request(productId, 5);

        when(apiService.getProductData(1L)).thenReturn(activeProductWithStock(10));
        when(cartItemRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(cartItemRepository.findByProductIdAndUserId(productId, userId))
                .thenReturn(Optional.empty());

        // When
        cartItemService.addToCart(userId, request);

        // Then: a new line is created with quantity 5
        var cartItemCaptor = org.mockito.ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(cartItemCaptor.capture());
        assertEquals(5, cartItemCaptor.getValue().getQuantity());
        assertEquals(productId, cartItemCaptor.getValue().getProductId());
        assertEquals(userId, cartItemCaptor.getValue().getUserId());
        assertEquals(BigDecimal.valueOf(500), cartItemCaptor.getValue().getPrice());
    }

    @Test
    void shouldRejectAddToExistingCartWhenFinalQuantityExceedsStock() {
        // Given: stock = 10, existing cart quantity = 7, requested quantity = 4
        String userId = "user-1";
        String productId = "1";
        CartItem existingCartItem = cartItem(userId, productId, 7);
        CartItemRequest request = request(productId, 4);

        when(apiService.getProductData(1L)).thenReturn(activeProductWithStock(10));
        when(cartItemRepository.findAllByUserId(userId)).thenReturn(List.of(existingCartItem));

        // When + Then: 7 + 4 = 11, which exceeds stock 10
        assertThrows(
                ProductOutOfStockException.class,
                () -> cartItemService.addToCart(userId, request)
        );

        verify(cartItemRepository, never()).save(any());
    }

    private CartItem cartItem(String userId, String productId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(BigDecimal.valueOf(quantity * 100L));
        return cartItem;
    }

    private CartItemRequest request(String productId, int quantity) {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }

    private ProductResponse activeProductWithStock(int stockQuantity) {
        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setActive(true);
        product.setStockQuantity(stockQuantity);
        product.setPrice(BigDecimal.valueOf(100));
        return product;
    }
}
