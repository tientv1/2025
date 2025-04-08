package com.assignment.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.entity.User;
import com.assignment.service.UserService;

@Controller
@RequestMapping("/admin/account")
public class AdminAccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String index(Model model, 
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {
        Page<User> accountPage = userService.getAllUsers(PageRequest.of(page, size));
        model.addAttribute("accounts", accountPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", accountPage.getTotalPages());
        model.addAttribute("totalItems", accountPage.getTotalElements());
        return "admin/account";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", false);
        return "admin/account-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/account/index";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("isEdit", true);
        return "admin/account-form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            user.setId(id); // Đảm bảo set ID cho user
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "Cập nhật tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/account/index";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái tài khoản!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/account/index";
    }
}