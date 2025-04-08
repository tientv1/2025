package com.assignment.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.service.AccountService;
import com.assignment.entity.User;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin("*")
public class AccountRestController {
    @Autowired
    AccountService accountService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getAccount(@PathVariable("username") String username) {
        User account = accountService.getAccountByUsername(username);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User account) {
        // Check if username already exists
        User existingAccount = accountService.getAccountByUsername(account.getUsername());
        if (existingAccount != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User savedAccount = accountService.saveAccount(account);
        return ResponseEntity.ok(savedAccount);
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> update(@PathVariable("username") String username, @RequestBody User account) {
        if (!username.equals(account.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        User existingAccount = accountService.getAccountByUsername(username);
        if (existingAccount == null) {
            return ResponseEntity.notFound().build();
        }
        User updatedAccount = accountService.saveAccount(account);
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        List<User> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
}
