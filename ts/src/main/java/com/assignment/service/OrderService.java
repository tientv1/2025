package com.assignment.service;

import com.assignment.entity.*;
import com.assignment.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order updatedOrder) {
        Order existingOrder = orderRepository.findById(updatedOrder.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setStatus(updatedOrder.getStatus());
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    public void createOrder(String username, HttpSession session) {
        User user = userService.getAccountByUsername(username);
        Map<Product, Integer> cart = (Map<Product, Integer>) session.getAttribute("cart");

        if (cart != null && !cart.isEmpty()) {
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");

            List<OrderDetail> details = cart.entrySet().stream()
                    .map(entry -> {
                        OrderDetail detail = new OrderDetail();
                        detail.setOrder(order);
                        detail.setProduct(entry.getKey());
                        detail.setQuantity(entry.getValue());
                        detail.setPrice(entry.getKey().getPrice());
                        return detail;
                    }).toList();

            order.setOrderDetails(details);
            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsername(username);
    }

    public List<OrderDetail> getPurchasedProducts(String username) {
        List<Order> orders = orderRepository.findByUserUsername(username);
        return orders.stream()
                .flatMap(order -> order.getOrderDetails().stream()).toList();
    }

    public Page<Order> getAllOrdersPaged(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return orderRepository.findAll(pageRequest);
    }

    public Map<String, Double> getMonthlyRevenue() {
        return orderRepository.findMonthlyRevenue().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).doubleValue()));
    }

    public Long getTotalOrders() {
        return orderRepository.count();
    }
}
