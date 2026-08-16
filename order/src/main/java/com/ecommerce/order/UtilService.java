package com.ecommerce.order;

import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.service.ExternalAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final ExternalAPIService apiService;

    public UserResponse validateUser(String userId) {
        UserResponse userResponse = apiService.getUser(userId);
        return userResponse;
    }


}
