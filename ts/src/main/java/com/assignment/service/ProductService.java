package com.assignment.service;

import com.assignment.entity.Product;
import com.assignment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        System.out.println("DEBUG - Found products: " + products.size());
        products.forEach(p -> System.out.println("DEBUG - Product: " + p.getName() + ", " + p.getPrice()));
        return products;
    }

    public Page<Product> getAllProductsPaged(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return productRepository.findAll(pageRequest);
    }

    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    public List<Product> getProductsByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String searchTerm = "%" + keyword.toLowerCase() + "%";
        return productRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    public List<Product> getRandomProducts(int limit) {
        return productRepository.findRandomProducts(limit);
    }

    public List<Product> getProductsByCategoryAndSort(Integer categoryId, String sort) {
        return productRepository.findByCategoryIdAndSort(categoryId, sort);
    }
}
