package com.assignment.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RevenueReportDTO {
    private Double totalRevenue;
    private Integer totalOrders;
    private Double avgOrderValue;
    private Map<String, Double> monthlyRevenue;
}