package com.assignment.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.assignment.entity.Category;
import com.assignment.service.CategoryService;

@Controller
@RequestMapping("/admin/category")
public class CategoryAdminController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/index")
    public String index(Model model, 
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {
        Page<Category> pageCategories = categoryService.getAllCategoriesPaged(page, size);
        model.addAttribute("categories", pageCategories.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageCategories.getTotalPages());
        return "admin/category";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        return "admin/category-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Tạo danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi tạo danh mục!");
        }
        return "redirect:/admin/category/index";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        model.addAttribute("isEdit", true);
        return "admin/category-form";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("success", "Cập nhập danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhập danh mục!");
        }
        return "redirect:/admin/category/index";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa danh mục!");
        }
        return "redirect:/admin/category/index";
    }
} 