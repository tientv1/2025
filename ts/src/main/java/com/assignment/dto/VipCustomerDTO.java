package com.assignment.dto;

import lombok.Data;

@Data
public class VipCustomerDTO {
    private Integer id;
    private String fullName;
    private String email;
    private Integer totalOrders;
    private Double totalSpent;
    private Double avgOrderValue;
}