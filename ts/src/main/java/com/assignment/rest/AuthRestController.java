package com.assignment.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.service.AccountService;
import com.assignment.dto.*;
import com.assignment.entity.User;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthRestController {
    @Autowired
    AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        User user = accountService.getAccountByUsername(loginRequest.getUsername());
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            LoginResponseDTO response = new LoginResponseDTO();

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());

            response.setUser(userDTO);
            response.setToken("Bearer " + user.getUsername());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        try {

            User user = accountService.getAllAccounts().stream()
                .filter(u -> request.getEmail().equals(u.getEmail()))
                .findFirst()
                .orElse(null);
                
            if (user == null) {
                return ResponseEntity.badRequest().body("Email not found");
            }

            return ResponseEntity.ok("Password reset email sent to " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }
}
