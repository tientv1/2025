package com.assignment.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.entity.Order;
import com.assignment.rest.base.OrderBaseRestController;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderRestController extends OrderBaseRestController {

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return super.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Integer id) {
        return super.getById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable("userId") Integer userId) {
        return super.getOrdersByUserId(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Integer id, @RequestBody Order order) {
        return super.update(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Integer id) {
        return super.delete(id);
    }
}
