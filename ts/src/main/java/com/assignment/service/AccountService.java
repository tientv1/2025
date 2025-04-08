package com.assignment.service;

import com.assignment.entity.User;
import com.assignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllAccounts() {
        return userRepository.findAll();
    }

    public User getAccountById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getAccountByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User saveAccount(User user) {
        return userRepository.save(user);
    }

    public void deleteAccount(Integer id) {
        userRepository.deleteById(id);
    }
}
