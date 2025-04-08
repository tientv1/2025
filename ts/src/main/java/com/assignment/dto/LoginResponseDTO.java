package com.assignment.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private UserDTO user;
}