package com.example.testTask.service;

import com.example.testTask.model.Category;
import com.example.testTask.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /*
        Получение списка всех уникальных категорий
     */
    public List<Category> findAllUniqueCategories() {
        return categoryRepository.findAllUniqueCategories();
    }
}