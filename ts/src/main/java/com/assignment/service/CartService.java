package com.assignment.service;

import com.assignment.entity.Product;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {
    private Map<Integer, CartItem> cart = new HashMap<>();

    public void addToCart(Product product, int quantity) {
        if (cart.containsKey(product.getId())) {
            cart.get(product.getId()).increaseQuantity(quantity);
        } else {
            cart.put(product.getId(), new CartItem(product, quantity));
        }
    }

    public void removeFromCart(Integer productId) {
        cart.remove(productId);
    }

    public void updateCart(Integer productId, int quantity) {
        if (cart.containsKey(productId)) {
            cart.get(productId).setQuantity(quantity);
        }
    }

    public Map<Integer, CartItem> getCart() {
        return cart;
    }
}
