package com.ecommerce.order.service;


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

    public void addToCart(String userId, CartItemRequest request) {

        ProductResponse productResponse=apiService.getProductData(Long.parseLong(
                request.getProductId()));
        validateProduct(request,productResponse);
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

    private void validateProduct(CartItemRequest request, ProductResponse productResponse) {
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

//    private User getUser(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
//    }
//
//    private Product checkProductExistsAndQuantity(CartItemRequest cartItemRequest) {
//        Product product= productRepository.findById(cartItemRequest.getProductId())
//                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
//
//        Integer productStockQuantity=product.getStockQuantity();
//        if(cartItemRequest.getQuantity()>productStockQuantity){
//            throw new ProductOutOfStock(product.getName()+" is out of stock");
//        }
//        return product;
//    }

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
        return cartItemRepository.findAllByUserId(userId)
                .stream()
                .map(cartItem -> mapToCartResponse(cartItem, userId))
                .toList();

    }

    public List<CartItem> getCart(String userId){
        return cartItemRepository.findAllByUserId(userId);
    }

    private CartItemResponse mapToCartResponse(CartItem cartItem, String userId){

//        String userName = Stream.of(user.getFirstName(), user.getLastName())
//                .filter(Objects::nonNull)
//                .collect(Collectors.joining(" "));
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .userId(Long.parseLong(userId))
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }
}
