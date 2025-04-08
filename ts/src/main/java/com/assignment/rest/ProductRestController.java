package com.assignment.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.rest.base.ProductBaseRestController;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductRestController extends ProductBaseRestController {

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return super.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") Integer id) {
        return super.getById(id);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("categoryId") Integer categoryId) {
        return super.getProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam("keyword") String keyword) {
        return super.searchProducts(keyword);
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getPagedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return super.getPagedProducts(page, size);
    }
}
