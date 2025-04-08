package com.assignment.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.service.CartService;
import com.assignment.service.ProductService;
import com.assignment.service.CartItem;
import com.assignment.entity.Product;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartRestController {
    @Autowired
    CartService cartService;
    
    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<?> getCart() {
        Map<Integer, CartItem> cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addToCart(
            @PathVariable("productId") Integer productId,
            @RequestParam(defaultValue = "1") int quantity) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        cartService.addToCart(product, quantity);
        return ResponseEntity.ok(cartService.getCart());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateCart(
            @PathVariable("productId") Integer productId,
            @RequestParam int quantity) {
        if (quantity < 1) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0");
        }
        cartService.updateCart(productId, quantity);
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable("productId") Integer productId) {
        cartService.removeFromCart(productId);
        return ResponseEntity.ok(cartService.getCart());
    }
}
