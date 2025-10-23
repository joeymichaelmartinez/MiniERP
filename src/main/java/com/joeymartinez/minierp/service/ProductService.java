package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.model.Product;
import com.joeymartinez.minierp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product CreateProduct(Product product) {
        return productRepository.save(product);
    }
}
