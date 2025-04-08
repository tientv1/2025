    package com.assignment.controller;

import com.assignment.entity.Category;
import com.assignment.entity.Product;
import com.assignment.entity.Review;
import com.assignment.service.CategoryService;
import com.assignment.service.ProductService;
import com.assignment.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.assignment.entity.User;
import com.assignment.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping({ "/home/index", "/" })
    public String index(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) { // 12 sản phẩm/trang
        Page<Product> pageProducts = productService.getAllProductsPaged(page, size);
        List<Category> categories = categoryService.getAllCategories();
        List<Product> randomProducts = productService.getRandomProducts(8);

        model.addAttribute("randomProducts", randomProducts);
        model.addAttribute("products", pageProducts.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        return "home";
    }

    @PostMapping("/product/detail/{id}/review")
    public String addReview(@PathVariable Integer id,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đánh giá sản phẩm.");
            return "redirect:/login";
        }

        Product product = productService.getProductById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại.");
            return "redirect:/";
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedDate(new Date());

        reviewService.saveReview(review);

        redirectAttributes.addFlashAttribute("success", "Đánh giá sản phẩm thành công!");
        return "redirect:/product/detail/" + id;
    }

    // Hiển thị chi tiết sản phẩm
    @GetMapping("/product/detail/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            List<Review> reviews = reviewService.getReviewsByProductId(id);
            model.addAttribute("reviews", reviews);
            model.addAttribute("product", product);
            model.addAttribute("title", product.getName());
            return "product-detail";
        }
        return "redirect:/";
    }

    // Hiển thị sản phẩm theo danh mục
    @GetMapping("/product/list-by-category/{id}")
    public String listByCategory(@PathVariable Integer id,
                               @RequestParam(required = false, defaultValue = "name_asc") String sort,
                               Model model) {
        List<Product> products = productService.getProductsByCategoryAndSort(id, sort);
        Category category = categoryService.getCategoryById(id);

        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("title", "Products - " + category.getName());
        return "product-list";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        List<Product> searchResults = productService.searchProducts(keyword);
        model.addAttribute("products", searchResults);
        model.addAttribute("keyword", keyword);
        return "product-list";
    }
}
