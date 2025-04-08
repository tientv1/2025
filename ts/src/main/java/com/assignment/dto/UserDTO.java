package com.assignment.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String fullname;
    private String role;
    private boolean active;
}