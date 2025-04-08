package com.assignment.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStats {
    private String fullName;
    private String email;
    private Long totalOrders;
    private Double totalSpent;
    private Double avgOrderValue;
} 