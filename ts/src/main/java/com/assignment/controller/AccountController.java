package com.assignment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.assignment.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import com.assignment.entity.User;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("user", new User());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveAccount(user);
            redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công!");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account/sign-up";
        }
    }

    @GetMapping("/edit-profile")
    public String editProfileForm(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        User user = userService.getAccountByUsername(username);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(@ModelAttribute User user,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            User currentUser = userService.getAccountByUsername(username);

            // Cập nhật thông tin mới
            currentUser.setFullname(user.getFullname());
            currentUser.setEmail(user.getEmail());

            // Sử dụng updateUser thay vì saveAccount
            userService.updateUser(currentUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/account/edit-profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account/edit-profile";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if (userService.changePassword(username, oldPassword, newPassword)) {
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/account/change-password";
        }
    }
}