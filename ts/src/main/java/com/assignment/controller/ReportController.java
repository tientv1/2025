package com.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.assignment.dto.UserStats;
import com.assignment.service.OrderService;
import com.assignment.service.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/revenue")
    public String revenue(Model model) {
        // Thống kê doanh thu theo tháng trong năm hiện tại
        Map<String, Double> monthlyRevenue = orderService.getMonthlyRevenue();

        // Tổng doanh thu
        Double totalRevenue = monthlyRevenue.values().stream().mapToDouble(Double::doubleValue).sum();

        // Số đơn hàng
        Long totalOrders = orderService.getTotalOrders();

        // Đơn hàng trung bình
        Double avgOrderValue = totalRevenue / totalOrders;

        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("avgOrderValue", avgOrderValue);

        return "admin/report/revenue";
    }

    @GetMapping("/vip")
    public String vipCustomers(Model model) {
        // Top khách hàng theo doanh số
        List<UserStats> vipCustomers = userService.getTopCustomersByRevenue(10);

        model.addAttribute("vipCustomers", vipCustomers);
        return "admin/report/vip";
    }
}