package com.assignment.service;

import com.assignment.dto.UserStats;
import com.assignment.entity.User;
import com.assignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }

    public boolean login(String username, String password, HttpSession session) {
        log.info("Login attempt - username: {}", username);

        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.isActive() && password.equals(user.getPassword())) {
            session.setAttribute("username", username);
            session.setAttribute("fullName", user.getFullname());
            session.setAttribute("role", user.getRole());
            return true;
        }
        return false;
    }

    public void logout(HttpSession session) {
        session.removeAttribute("username");
        session.removeAttribute("fullName");
        session.removeAttribute("role");
        session.invalidate();
    }

    public User getAccountByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void saveAccount(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        userRepository.save(user);
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = getAccountByUsername(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void createUser(User user) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã được sử dụng!");
        }
        
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // Set default values
        user.setActive(true);
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        userRepository.save(user);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        // Kiểm tra username mới có trùng với user khác không
        userRepository.findByUsername(user.getUsername())
            .ifPresent(u -> {
                if (!u.getId().equals(user.getId())) {
                    throw new RuntimeException("Tên đăng nhập đã được sử dụng!");
                }
            });

        // Kiểm tra email mới có trùng với user khác không  
        userRepository.findByEmail(user.getEmail())
            .ifPresent(u -> {
                if (!u.getId().equals(user.getId())) {
                    throw new RuntimeException("Email đã được sử dụng!");
                }
            });

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullname(user.getFullname());
        existingUser.setRole(user.getRole());
        existingUser.setActive(user.isActive());

        userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        User user = getUserById(id);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    public Page<User> getAllUsers(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<UserStats> getTopCustomersByRevenue(int limit) {
        return userRepository.findTopCustomersByRevenue(limit)
            .stream()
            .map(row -> new UserStats(
                (String) row[0],  // fullname
                (String) row[1],  // email
                ((Number) row[2]).longValue(),  // total_orders
                ((Number) row[3]).doubleValue(),  // total_spent
                ((Number) row[4]).doubleValue()   // avg_order
            ))
            .collect(Collectors.toList());
    }

    public void toggleUserStatus(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}
