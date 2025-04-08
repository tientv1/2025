package com.assignment.controller;

import com.assignment.entity.Product;
import com.assignment.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @GetMapping("")
    public String viewCart(Model model, HttpSession session) {
        Map<Product, Integer> cartItems = getCartFromSession(session);
        double total = cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("title", "Shopping Cart");
        return "cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Integer id,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            if (product != null) {
                Map<Product, Integer> cart = getCartFromSession(session);

                // Kiểm tra nếu sản phẩm đã có trong giỏ hàng
                boolean productExists = false;
                for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                    if (entry.getKey().getId().equals(product.getId())) {
                        // Cập nhật số lượng
                        entry.setValue(entry.getValue() + quantity);
                        productExists = true;
                        break;
                    }
                }

                // Nếu sản phẩm chưa có trong giỏ hàng
                if (!productExists) {
                    cart.put(product, quantity);
                }

                session.setAttribute("cart", cart);
                redirectAttributes.addFlashAttribute("success",
                        "Added " + quantity + " " + product.getName() + " to cart");
            } else {
                redirectAttributes.addFlashAttribute("error", "Product not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding product to cart");
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Map<Product, Integer> cart = getCartFromSession(session);
            Product productToRemove = null;
            
            // Tìm sản phẩm cần xóa
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                if (entry.getKey().getId().equals(id)) {
                    productToRemove = entry.getKey();
                    break;
                }
            }
            
            // Xóa sản phẩm khỏi giỏ hàng
            if (productToRemove != null) {
                cart.remove(productToRemove);
                session.setAttribute("cart", cart);
                redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm khỏi giỏ hàng");
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be greater than 0");
            return "redirect:/cart";
        }

        Map<Product, Integer> cart = getCartFromSession(session);
        boolean updated = false;

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            if (entry.getKey().getId().equals(productId)) {
                entry.setValue(quantity);
                updated = true;
                break;
            }
        }

        if (updated) {
            session.setAttribute("cart", cart);
            redirectAttributes.addFlashAttribute("success", "Cart updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found in cart");
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("success", "Cart cleared successfully");
        return "redirect:/cart";
    }

    @SuppressWarnings("unchecked")
    private Map<Product, Integer> getCartFromSession(HttpSession session) {
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}