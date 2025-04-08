package com.assignment.dto;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;
}