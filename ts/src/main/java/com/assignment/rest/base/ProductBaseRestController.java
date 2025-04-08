package com.assignment.rest.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import com.assignment.service.ProductService;
import com.assignment.entity.Product;
import java.util.List;

/**
 * Lớp cơ sở cho các Product REST controller
 */
public abstract class ProductBaseRestController extends BaseRestController<Product, Integer> {
    
    @Autowired
    protected ProductService productService;
    
    @Override
    protected List<Product> getAllEntities() {
        return productService.getAllProducts();
    }
    
    @Override
    protected Product getEntityById(Integer id) {
        return productService.getProductById(id);
    }
    
    @Override
    protected Product saveEntity(Product product) {
        return productService.saveProduct(product);
    }
    
    @Override
    protected void deleteEntity(Integer id) {
        productService.deleteProduct(id);
    }
    
    @Override
    protected boolean isValidId(Integer id, Product product) {
        return id.equals(product.getId());
    }
    
    public ResponseEntity<?> getProductsByCategory(Integer categoryId) {
        try {
            List<Product> products = productService.getProductsByCategoryId(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    public ResponseEntity<?> searchProducts(String keyword) {
        try {
            List<Product> products = productService.searchProducts(keyword);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    public ResponseEntity<?> getPagedProducts(int page, int size) {
        try {
            Page<Product> products = productService.getAllProductsPaged(page, size);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
