package com.ecommerce.order.service;


import com.ecommerce.order.UtilService;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.CartItemResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exceptions.ProductInValidException;
import com.ecommerce.order.exceptions.ProductOutOfStockException;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ExternalAPIService apiService;
    private final UtilService utilService;

    public void addToCart(String userId, CartItemRequest request) {

        //validate user
        utilService.validateUser(userId);

        //validate product
        ProductResponse productResponse=apiService.getProductData(Long.parseLong(
                request.getProductId()));
        utilService.validateProduct(request,productResponse);

        // checking whether the product is already added to card by the user if yes add the quantity
        CartItem cartItem=cartItemRepository.findByProductIdAndUserId(request.getProductId(),userId)
                .orElse(new CartItem());
        cartItem.setProductId(request.getProductId());
        cartItem.setUserId(userId);

        if(Objects.nonNull(cartItem.getQuantity())){
            Integer totalQuantity=Integer.sum(cartItem.getQuantity(), request.getQuantity());
            cartItem.setQuantity(totalQuantity);
            BigDecimal  totalProductPrice= BigDecimal.valueOf(totalQuantity).multiply(productResponse.getPrice());
            cartItem.setPrice(totalProductPrice);
        }
        else{
            cartItem.setQuantity(request.getQuantity());
            BigDecimal  totalProductPrice= BigDecimal.valueOf(request.getQuantity()).multiply(productResponse.getPrice());
            cartItem.setPrice(totalProductPrice);
        }

        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(String userId, String productId) {
//        User user = getUser(Long.parseLong(userId));
//        Product product= productRepository.findById(productId)
//                .orElseThrow(()->new ResourceNotFoundException("Product not found"));

        Optional<CartItem> cartItem = cartItemRepository.findByProductIdAndUserId(productId,userId);
        if(Objects.nonNull(cartItem)){
            cartItemRepository.delete(cartItem.get());
        }

    }

    public void clearCartForUser(String userId){
        cartItemRepository.deleteCartItemByUserId(userId);
    }

    public List<CartItemResponse> getCartOfUser(String userId) {
        utilService.validateUser(userId);
        return cartItemRepository.findAllByUserId(userId)
                .stream()
                .map(cartItem -> mapToCartResponse(cartItem, userId))
                .toList();

    }

    public List<CartItem> getCart(String userId){
        return cartItemRepository.findAllByUserId(userId);
    }

    private CartItemResponse mapToCartResponse(CartItem cartItem, String userId){
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .userId(userId)
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }
}
