package com.assignment.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private String status = "PENDING";
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
    
    @Transient
    public Double getTotal() {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return 0.0;
        }
        return orderDetails.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
