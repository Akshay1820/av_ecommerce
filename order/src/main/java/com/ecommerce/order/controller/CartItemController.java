package com.ecommerce.order.controller;


import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.CartItemResponse;
import com.ecommerce.order.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<String> createCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest request) {
        cartItemService.addToCart(userId, request);
        return ResponseEntity.ok("Cart added");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteCartItem(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable String productId) {
        cartItemService.deleteCartItem(userId, productId);
        return ResponseEntity.ok("Item is removed from the cart");
    }


    @GetMapping("/user")
    public ResponseEntity<List<CartItemResponse>> getUserCart(
            @RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(
                cartItemService.getCartOfUser(userId));
    }


}
