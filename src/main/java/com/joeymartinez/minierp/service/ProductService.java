package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.model.Product;
import com.joeymartinez.minierp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts(String name) {
        if (name == null || name.isBlank()) {
            return productRepository.findAll();
        } else {
            return productRepository.findByNameContainingIgnoreCase(name);
        }
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product CreateProduct(Product product) {
        return productRepository.save(product);
    }
}
