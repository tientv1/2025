package com.assignment.controller;

import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;
import com.assignment.entity.User;
import com.assignment.service.OrderService;
import com.assignment.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Enumeration;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        
        // Debug logs
        System.out.println("Session ID: " + session.getId());
        System.out.println("All session attributes:");
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            System.out.println(name + ": " + session.getAttribute(name));
        }
        
        if (username == null) {
            session.setAttribute("redirectUrl", "/order/checkout");
            return "redirect:/auth/login";
        }
        
        User user = userService.getAccountByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("cart", session.getAttribute("cart"));
        return "check-out";
    }

    @PostMapping("/checkout")
    public String processCheckout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        orderService.createOrder(username, session);
        session.removeAttribute("cart"); // Clear cart after order
        return "redirect:/order/list";
    }

    @GetMapping("/list")
    public String orderList(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.getOrdersByUsername(username);
        model.addAttribute("orders", orders);
        return "order-list";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Integer id, 
                            HttpSession session, 
                            Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        Order order = orderService.getOrderById(id);
        // Check if order belongs to user
        if (!order.getUser().getUsername().equals(username)) {
            return "redirect:/order/list";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", order.getOrderDetails());
        return "order-detail";
    }

    @GetMapping("/my-product-list")
    public String myProductList(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/auth/login";
        }

        List<OrderDetail> purchasedProducts = orderService.getPurchasedProducts(username);
        model.addAttribute("purchasedProducts", purchasedProducts);
        return "my-product-list";
    }
} 