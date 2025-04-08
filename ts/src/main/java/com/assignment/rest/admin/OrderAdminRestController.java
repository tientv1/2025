package com.assignment.rest.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.entity.Order;
import com.assignment.rest.base.OrderBaseRestController;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin("*")
public class OrderAdminRestController extends OrderBaseRestController {

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return super.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Integer id) {
        return super.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("id") Integer id, @RequestBody Order order) {
        return super.update(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Integer id) {
        return super.delete(id);
    }
}
