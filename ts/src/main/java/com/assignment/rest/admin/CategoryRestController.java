package com.assignment.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.assignment.service.CategoryService;
import com.assignment.entity.Category;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin("*")
public class CategoryRestController {
    @Autowired
    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getPagedCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.getAllCategoriesPaged(page, size);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable("id") Integer id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.ok(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Integer id, @RequestBody Category category) {
        if (!id.equals(category.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Category existingCategory = categoryService.getCategoryById(id);
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        Category updatedCategory = categoryService.saveCategory(category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) {
        Category existingCategory = categoryService.getCategoryById(id);
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
