package com.ecommerce.product.service;


import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest){
        Product product=new Product();
        mapToProduct(product,productRequest);
        productRepository.save(product);
        return mapToProductResponse(product);
    }

    private Product mapToProduct(Product product,ProductRequest productRequest){
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setActive(productRequest.getActive());
        return product;
    }

    private ProductResponse mapToProductResponse(Product product){
       return ProductResponse.builder()
               .id(product.getId())
               .name(product.getName())
               .description(product.getDescription())
               .price(product.getPrice())
               .stockQuantity(product.getStockQuantity())
               .category(product.getCategory())
               .imageUrl(product.getImageUrl())
               .active(product.getActive())
               .build();
    }

    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
    }

    public  List<ProductResponse> getAllProducst() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Long id,ProductRequest productRequest) {
        Product product=productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
        mapToProduct(product,productRequest);
        productRepository.save(product);
        return mapToProductResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product=productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    public List<ProductResponse> searchProduct(String search) {
        return productRepository.searchProducts(search)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

    }
}
