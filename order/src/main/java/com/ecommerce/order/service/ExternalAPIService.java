package com.ecommerce.order.service;

import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exceptions.ProductNotAvailableException;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalAPIService {

    @Value("${product.service.url}")
    public String url;

    private final RestTemplate restTemplate;

    public ProductResponse getProductData(Long productId){
        try {
            String getUrl = url + "/" + productId;
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(getUrl, ProductResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotAvailableException("Product " + productId + " not found");
        } catch (HttpClientErrorException e) {
            // catches other 4xx errors (400, 401, 403, etc.)
            throw new RuntimeException("Client error while fetching product: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            // catches 5xx errors from the product service
            throw new RuntimeException("Product service is unavailable", e);
        } catch (ProductNotAvailableException e) {
            // connection refused, timeout, etc.
            throw new RuntimeException("Could not connect to product service", e);
        }
    }
}
