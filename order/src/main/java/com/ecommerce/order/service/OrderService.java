package com.ecommerce.order.service;


import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemService cartItemService;
    private final OrderRepository orderRepository;
    private final ExternalAPIService apiService;

    public OrderResponse createOrder(String userId) {

        UserResponse userResponse=validateUser(userId);

        // Validate for cart items
        List<CartItem> cartItemList = cartItemService.getCart(userId);

        // Calculate total price

        BigDecimal totalAmount=cartItemList.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        //Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        List<OrderItem> orderItem = cartItemList.stream()
                .map(cartItem -> cartItemToOrderItem(cartItem,order))
                .toList();

        order.setOrderItems(orderItem);
        orderRepository.save(order);

        //Clear the cart
        cartItemService.clearCartForUser(userId);

        return mapToOrderResponse(order);
    }

    private UserResponse validateUser(String userId) {
        UserResponse userResponse = apiService.getUser(userId);
        return userResponse;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderStatus(order.getOrderStatus());
        orderResponse.setItems(getOrItemDto(order));
        orderResponse.setCreatedAt(order.getCreatedAt());
        orderResponse.setTotalAmount(order.getTotalAmount());
        return orderResponse;
    }

    private List<OrderItemDTO> getOrItemDto(Order order) {
       return order.getOrderItems().stream()
                .map(orderItem -> {
                   return OrderItemDTO.builder()
                            .id(orderItem.getId())
                            .productId(orderItem.getProductId())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .build();
                })
                .toList();
    }

    private OrderItem cartItemToOrderItem(CartItem cartItem,Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(cartItem.getProductId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getPrice());
        return orderItem;
    }


//    private User getUser(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
//    }
}
