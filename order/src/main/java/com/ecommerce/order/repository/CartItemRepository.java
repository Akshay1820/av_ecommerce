package com.ecommerce.order.repository;


import com.ecommerce.order.model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    Optional<CartItem> findByProductIdAndUserId(String productId, String userId);


    @Modifying
    @Transactional
    void deleteCartItemByProductIdAndUserId(@Param("productId") Long productId,
                                        @Param("userId") Long userId);

    List<CartItem> findAllByUserId(String userId);

    @Modifying
    @Transactional
    void deleteCartItemByUserId(String userId);
}
