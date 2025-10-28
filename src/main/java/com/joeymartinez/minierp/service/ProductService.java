package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.dto.ProductUpdateDTO;
import com.joeymartinez.minierp.model.Product;
import com.joeymartinez.minierp.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import static com.joeymartinez.minierp.util.UpdateUtils.updateIfPresent;

import java.sql.Timestamp;
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

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("No product found"));
        updateIfPresent(productUpdateDTO.getName(), existingProduct::setName);
        updateIfPresent(productUpdateDTO.getStock(), existingProduct::setStock);
        updateIfPresent(productUpdateDTO.getPrice(), existingProduct::setPrice);
        existingProduct.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("No product found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
