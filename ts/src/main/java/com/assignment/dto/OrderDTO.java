package com.assignment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Integer id;
    private LocalDateTime orderDate;
    private String status;
    private Double total;
    private List<OrderDetailDTO> orderDetails;
}