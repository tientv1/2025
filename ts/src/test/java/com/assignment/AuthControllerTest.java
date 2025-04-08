package com.assignment;

import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.assignment.controller.AccountController;
import com.assignment.controller.AuthController;
import com.assignment.service.UserService;
import com.assignment.service.MailService;
import com.assignment.entity.User;
import com.assignment.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private JavaMailSender mailSender;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private AccountController accountController;

    @Test()
    public void testLoginSuccessAdmin() {
        when(userService.login("admin", "123456", session)).thenReturn(true);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        String result = authController.login("admin", "123456", session, redirectAttributes);
        assertEquals(result, "redirect:/admin/product/index");
        verify(userService).login("admin", "123456", session);
    }

    @Test()
    public void testLoginSuccessUser() {
        when(userService.login("user", "123456", session)).thenReturn(true);
        when(session.getAttribute("role")).thenReturn("USER");
        String result = authController.login("user", "123456", session, redirectAttributes);
        assertEquals(result, "redirect:/");
        verify(userService).login("user", "123456", session);
    }

    @Test()
    public void testLoginFailure() {
        when(userService.login("invalid", "wrong", session)).thenReturn(false);
        String result = authController.login("invalid", "wrong", session, redirectAttributes);
        assertEquals(result, "redirect:/auth/login?error");
        verify(userService).login("invalid", "wrong", session);
    }

    @Test()
    public void testLogoutSuccess() {
        String result = authController.logout(session);
        verify(session).invalidate();
        assertEquals(result, "redirect:/auth/login?logout");
    }

    @Test()
    public void testForgotPasswordValidEmail() {
        User mockUser = new User();
        mockUser.setEmail("tientranvan19052004@gmail.com");
        when(userService.getUserByEmail("tientranvan19052004@gmail.com")).thenReturn(mockUser);
        String result = authController.processForgotPassword("tientranvan19052004@gmail.com", redirectAttributes, session);
        verify(session).setAttribute(eq("resetEmail"), eq("tientranvan19052004@gmail.com"));
        verify(session).setAttribute(eq("resetCode"), anyString());
        verify(session).setMaxInactiveInterval(300);
        assertEquals(result, "redirect:/auth/verify-code");
    }

    @Test()
    public void testForgotPasswordInvalidEmail() {
        when(userService.getUserByEmail("invalid@example.com")).thenReturn(null);
        String result = authController.processForgotPassword("invalid@example.com", redirectAttributes, session);
        verify(redirectAttributes).addFlashAttribute("error", "Email không tồn tại trong hệ thống!");
        assertEquals(result, "redirect:/auth/forgot-password");
    }

    @Test()
    public void testVerifyResetCodeSuccess() throws Exception {
        String storedEmail = "test@example.com";
        String storedCode = "123456";
        when(session.getAttribute("resetEmail")).thenReturn(storedEmail);
        when(session.getAttribute("resetCode")).thenReturn(storedCode);
        when(templateEngine.process(eq("email/new-password"), any(Context.class))).thenReturn("email content");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        String result = authController.processVerifyCode("123456", session, redirectAttributes);
        verify(userService).resetPassword(eq(storedEmail), anyString());
        verify(session).removeAttribute("resetCode");
        verify(session).removeAttribute("resetEmail");
        verify(mailSender).send(any(MimeMessage.class));
        assertEquals(result, "redirect:/auth/login");
    }

    @Test()
    public void testVerifyResetCodeInvalidCode() {
        when(session.getAttribute("resetEmail")).thenReturn("test@example.com");
        when(session.getAttribute("resetCode")).thenReturn("123456");
        String result = authController.processVerifyCode("654321", session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Mã xác nhận không đúng!");
        assertEquals(result, "redirect:/auth/verify-code");
    }

    @Test()
    public void testVerifyResetCodeExpiredSession() {
        when(session.getAttribute("resetEmail")).thenReturn(null);
        when(session.getAttribute("resetCode")).thenReturn(null);
        String result = authController.processVerifyCode("123456", session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Phiên làm việc đã hết hạn!");
        assertEquals(result, "redirect:/auth/forgot-password");
    }

    @Test()
    public void testCreateTestUserSuccess() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        String result = authController.createTestUser();
        verify(userRepository).save(any(User.class));
        assertEquals(result, "redirect:/auth/login?message=Test user created");
    }

    @Test()
    public void testCreateTestUserAlreadyExists() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new User()));
        String result = authController.createTestUser();
        assertEquals(result, "redirect:/auth/login?message=User already exists");
    }

    @Test()
    public void testShowForgotPasswordForm() {
        String result = authController.showForgotPasswordForm();
        assertEquals(result, "forgot-password");
    }

    @Test()
    public void testShowVerifyCodeForm() {
        when(session.getAttribute("resetEmail")).thenReturn("admin@example.com");
        String result = authController.showVerifyCodeForm(session, redirectAttributes);
        assertEquals(result, "verify-code");
    }

    @Test()
    public void testLoginWithRedirectUrl() {
        when(userService.login("user", "123456", session)).thenReturn(true);
        when(session.getAttribute("redirectUrl")).thenReturn("/order/checkout");
        when(session.getAttribute("role")).thenReturn("USER");
        String result = authController.login("user", "123456", session, redirectAttributes);
        assertEquals(result, "redirect:/order/checkout");
        verify(session).removeAttribute("redirectUrl");
    }

    @Test()
    public void testShowLoginFormWithError() {
        String result = authController.loginForm("error", null, model);
        verify(model).addAttribute("error", "Tài khoản và đăng nhập không hợp lệ!");
        assertEquals(result, "login");
    }

    @Test()
    public void testShowLoginFormWithLogout() {
        String result = authController.loginForm(null, "logout", model);
        verify(model).addAttribute("success", "Bạn đã đăng xuất thành công.");
        assertEquals(result, "login");
    }

    @Test()
    public void testCreateTestUserFailure() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));
        String result = authController.createTestUser();
        assertEquals(result, "redirect:/auth/login?error=Error creating user");
    }

    @Test()
    public void testShowVerifyCodeFormWithoutEmail() {
        when(session.getAttribute("resetEmail")).thenReturn(null);
        String result = authController.showVerifyCodeForm(session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Vui lòng nhập email trước!");
        assertEquals(result, "redirect:/auth/forgot-password");
    }

    @Test()
    public void testResetPasswordEmailError() throws Exception {
        String storedEmail = "test@example.com";
        String storedCode = "123456";
        when(session.getAttribute("resetEmail")).thenReturn(storedEmail);
        when(session.getAttribute("resetCode")).thenReturn(storedCode);
        when(templateEngine.process(eq("email/new-password"), any(Context.class))).thenReturn("email content");
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server error"));
        String result = authController.processVerifyCode("123456", session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Có lỗi xảy ra: Mail server error");
        assertEquals(result, "redirect:/auth/verify-code");
    }

    @Test()
    public void testResetPasswordUserNotFound() throws Exception {
        String storedEmail = "test@example.com";
        String storedCode = "123456";
        when(session.getAttribute("resetEmail")).thenReturn(storedEmail);
        when(session.getAttribute("resetCode")).thenReturn(storedCode);
        doThrow(new RuntimeException("Không tìm thấy tài khoản với email này!"))
                .when(userService).resetPassword(eq(storedEmail), anyString());
        String result = authController.processVerifyCode("123456", session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Có lỗi xảy ra: Không tìm thấy tài khoản với email này!");
        assertEquals(result, "redirect:/auth/verify-code");
    }

    @Test()
    public void testUpdateProfileSuccess() {
        String username = "user";
        User currentUser = new User();
        currentUser.setUsername(username);

        User updatedUser = new User();
        updatedUser.setFullname("New Name");
        updatedUser.setEmail("new@example.com");

        when(session.getAttribute("username")).thenReturn(username);
        when(userService.getAccountByUsername(username)).thenReturn(currentUser);

        // When
        String result = accountController.editProfile(updatedUser, session, redirectAttributes);
        verify(userService).updateUser(any(User.class));
        verify(redirectAttributes).addFlashAttribute("success", "Cập nhật thông tin thành công!");
        assertEquals(result, "redirect:/account/edit-profile");
    }

    @Test()
    public void testUpdateProfileDuplicateEmail() {
        String username = "user";
        User currentUser = new User();
        currentUser.setUsername(username);
        User updatedUser = new User();
        updatedUser.setEmail("exists@example.com");
        when(session.getAttribute("username")).thenReturn(username);
        when(userService.getAccountByUsername(username)).thenReturn(currentUser);
        doThrow(new RuntimeException("Email đã được sử dụng!"))
                .when(userService).updateUser(any(User.class));
        String result = accountController.editProfile(updatedUser, session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Email đã được sử dụng!");
        assertEquals(result, "redirect:/account/edit-profile");
    }

    @Test()
    public void testChangePasswordSuccess() {
        when(session.getAttribute("username")).thenReturn("user");
        when(userService.changePassword("user", "oldpass", "newpass")).thenReturn(true);
        String result = accountController.changePassword("oldpass", "newpass", session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("success", "Đổi mật khẩu thành công!");
        assertEquals(result, "redirect:/");
    }

    @Test()
    public void testChangePasswordWrongOldPassword() {
        when(session.getAttribute("username")).thenReturn("user");
        when(userService.changePassword("user", "wrongpass", "newpass")).thenReturn(false);
        String result = accountController.changePassword("wrongpass", "newpass", session, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
        assertEquals(result, "redirect:/account/change-password");
    }

    @Test
    public void createProductTest() {
        
    }
}