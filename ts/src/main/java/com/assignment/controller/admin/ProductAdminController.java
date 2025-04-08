package com.assignment.controller.admin;

import com.assignment.entity.Product;
import com.assignment.entity.Category;
import com.assignment.service.ProductService;
import com.assignment.service.CategoryService;
import com.assignment.service.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class ProductAdminController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/index")
    public String index(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> pageProducts = productService.getAllProductsPaged(page, size);
        model.addAttribute("products", pageProducts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        return "admin/product";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        Product product = new Product();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Product product,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                product.setImage(imageUrl);
            }
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Tạo sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi tạo sản phẩm!" + e.getMessage());
        }
        return "redirect:/admin/product/index";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("isEdit", true);
        return "admin/product-form";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Product product,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadFile(file);
                product.setImage(imageUrl);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Cập nhập sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhập sản phẩm" + e.getMessage());
        }
        return "redirect:/admin/product/index";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Lỗi xóa sản phẩm" + id);
                return "redirect:/admin/product/index";
            }

            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa sản phẩm!" + e.getMessage());
        }
        return "redirect:/admin/product/index";
    }
}