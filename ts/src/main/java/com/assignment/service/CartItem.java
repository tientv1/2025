package com.assignment.service;

import com.assignment.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}
