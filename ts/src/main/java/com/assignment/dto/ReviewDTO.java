package com.assignment.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewDTO {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private int rating;
    private String comment;
    private Date createdDate;
}
