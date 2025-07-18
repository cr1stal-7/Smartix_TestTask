package com.example.testTask.controller;

import com.example.testTask.model.Category;
import com.example.testTask.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/unique")
    public ResponseEntity<List<Category>> getAllUniqueCategories() {
        return ResponseEntity.ok(categoryService.findAllUniqueCategories());
    }
}