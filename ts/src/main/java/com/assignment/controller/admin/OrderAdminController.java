package com.assignment.controller.admin;

import com.assignment.entity.Order;
import com.assignment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/index")
    public String index(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {
        Page<Order> pageOrders = orderService.getAllOrdersPaged(page, size);
        model.addAttribute("orders", pageOrders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageOrders.getTotalPages());
        return "admin/order";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("isEdit", true);
        return "admin/order-form";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Order order, RedirectAttributes redirectAttributes) {
        try {
            orderService.saveOrder(order);
            redirectAttributes.addFlashAttribute("success", "Order updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order");
        }
        return "redirect:/admin/order/index";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting order");
        }
        return "redirect:/admin/order/index";
    }
} 