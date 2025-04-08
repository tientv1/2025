package com.assignment.controller;

import com.assignment.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.assignment.entity.User;
import com.assignment.repository.UserRepository;
import java.util.Random;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.assignment.service.MailService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailService mailService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Tài khoản và đăng nhập không hợp lệ!");
        }
        if (logout != null) {
            model.addAttribute("success", "Bạn đã đăng xuất thành công.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (userService.login(username, password, session)) {
            String role = (String) session.getAttribute("role");
            String redirectUrl = (String) session.getAttribute("redirectUrl");

            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                return "redirect:" + redirectUrl;
            }

            if ("ADMIN".equals(role)) {
                return "redirect:/admin/product/index";
            }
            return "redirect:/";
        }
        return "redirect:/auth/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login?logout";
    }

    @GetMapping("/create-test-user")
    public String createTestUser() {
        // Kiểm tra xem user đã tồn tại chưa
        if (userRepository.findByUsername("admin").isPresent()) {
            return "redirect:/auth/login?message=User already exists";
        }

        try {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("123456"); // Mật khẩu không mã hóa
            user.setEmail("admin@gmail.com");
            user.setFullname("Administrator");
            user.setRole("ROLE_ADMIN");

            userRepository.save(user);
            return "redirect:/auth/login?message=Test user created";
        } catch (Exception e) {
            return "redirect:/auth/login?error=Error creating user";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống!");
            return "redirect:/auth/forgot-password";
        }

        // Tạo mã xác nhận
        String resetCode = String.format("%06d", new Random().nextInt(999999));

        // Lưu vào session
        session.setAttribute("resetEmail", email);
        session.setAttribute("resetCode", resetCode);
        session.setMaxInactiveInterval(300);

        mailService.sendResetCodeEmail(email, resetCode);

        redirectAttributes.addFlashAttribute("success", "Mã xác nhận đang được gửi đến email của bạn!");
        return "redirect:/auth/verify-code";
    }

    @GetMapping("/verify-code")
    public String showVerifyCodeForm(HttpSession session,
            RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập email trước!");
            return "redirect:/auth/forgot-password";
        }
        return "verify-code";
    }

    @PostMapping("/verify-code")
    public String processVerifyCode(@RequestParam String code,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String resetCode = (String) session.getAttribute("resetCode");
        String email = (String) session.getAttribute("resetEmail");

        if (resetCode == null || email == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên làm việc đã hết hạn!");
            return "redirect:/auth/forgot-password";
        }

        if (!resetCode.equals(code)) {
            redirectAttributes.addFlashAttribute("error", "Mã xác nhận không đúng!");
            return "redirect:/auth/verify-code";
        }

        try {
            String newPassword = generateRandomPassword();

            userService.resetPassword(email, newPassword);

            Context context = new Context();
            context.setVariable("newPassword", newPassword);

            String emailContent = templateEngine.process("email/new-password", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Mật khẩu mới của bạn");
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);

            session.removeAttribute("resetCode");
            session.removeAttribute("resetEmail");

            redirectAttributes.addFlashAttribute("success", "Mật khẩu mới đã được gửi đến email của bạn!");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/auth/verify-code";
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}