package com.assignment.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.service.AccountService;
import com.assignment.entity.User;
import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@CrossOrigin("*")
public class AdminAccountRestController {
    @Autowired
    AccountService accountService;

    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        List<User> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") Integer id) {
        User account = accountService.getAccountById(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody User account) {
        // Check if username already exists
        User existingAccount = accountService.getAccountByUsername(account.getUsername());
        if (existingAccount != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User savedAccount = accountService.saveAccount(account);
        return ResponseEntity.ok(savedAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") Integer id, @RequestBody User account) {
        if (!id.equals(account.getId())) {
            return ResponseEntity.badRequest().build();
        }
        User existingAccount = accountService.getAccountById(id);
        if (existingAccount == null) {
            return ResponseEntity.notFound().build();
        }
        User updatedAccount = accountService.saveAccount(account);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") Integer id) {
        User existingAccount = accountService.getAccountById(id);
        if (existingAccount == null) {
            return ResponseEntity.notFound().build();
        }
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }
}
