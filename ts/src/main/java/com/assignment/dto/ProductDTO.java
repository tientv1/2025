package com.assignment.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private Double price;
    private String image;
    // Add other fields as needed
}