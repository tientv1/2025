package com.assignment.rest.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.assignment.service.OrderService;
import com.assignment.entity.Order;
import java.util.List;

/**
 * Lớp cơ sở cho các Order REST controller
 */
public abstract class OrderBaseRestController extends BaseRestController<Order, Integer> {
    
    @Autowired
    protected OrderService orderService;
    
    @Override
    protected List<Order> getAllEntities() {
        return orderService.getAllOrders();
    }
    
    @Override
    protected Order getEntityById(Integer id) {
        return orderService.getOrderById(id);
    }
    
    @Override
    protected Order saveEntity(Order order) {
        return orderService.saveOrder(order);
    }
    
    @Override
    protected void deleteEntity(Integer id) {
        orderService.deleteOrder(id);
    }
    
    @Override
    protected boolean isValidId(Integer id, Order order) {
        return id.equals(order.getId());
    }
    
     public ResponseEntity<?> getOrdersByUserId(Integer userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
