package com.example.testTask.service;

import com.example.testTask.model.Category;
import com.example.testTask.model.Product;
import com.example.testTask.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category electronics;
    private Category clothing;
    private Category books;
    private Product laptop;
    private Product tShirt;

    @BeforeEach
    void setUp() {
        electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Electronics");

        clothing = new Category();
        clothing.setId(2L);
        clothing.setName("Clothing");

        books = new Category();
        books.setId(3L);
        books.setName("Books");

        laptop = new Product();
        laptop.setId(1L);
        laptop.setTitle("Laptop");
        laptop.setCategory(electronics);

        tShirt = new Product();
        tShirt.setId(2L);
        tShirt.setTitle("T-Shirt");
        tShirt.setCategory(clothing);
    }

    /**
     * Проверяет получение списка всех уникальных категорий
     */
    @Test
    void findAllUniqueCategories_ShouldReturnAllUniqueCategories() {
        List<Category> expectedCategories = List.of(electronics, clothing);
        when(categoryRepository.findAllUniqueCategories()).thenReturn(expectedCategories);

        List<Category> result = categoryService.findAllUniqueCategories();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expectedCategories));
        assertFalse(result.contains(books));
        verify(categoryRepository).findAllUniqueCategories();
    }

    /**
     * Проверяет случай, когда нет продуктов.
     * Должен вернуть пустой список
     */
    @Test
    void findAllUniqueCategories_WhenNoProductsExist_ShouldReturnEmptyList() {
        when(categoryRepository.findAllUniqueCategories()).thenReturn(List.of());

        List<Category> result = categoryService.findAllUniqueCategories();

        assertTrue(result.isEmpty());
        verify(categoryRepository).findAllUniqueCategories();
    }

    /**
     * Проверяет случай, когда несколько продуктов в одной категории.
     * Должен вернуть категорию только один раз
     */
    @Test
    void findAllUniqueCategories_WithMultipleProductsInSameCategory() {
        Product phone = new Product();
        phone.setId(3L);
        phone.setTitle("Phone");
        phone.setCategory(electronics);

        when(categoryRepository.findAllUniqueCategories()).thenReturn(List.of(electronics, clothing));

        List<Category> result = categoryService.findAllUniqueCategories();

        assertEquals(2, result.size());
        long electronicsCount = result.stream()
                .filter(c -> "Electronics".equals(c.getName()))
                .count();
        assertEquals(1, electronicsCount);
        verify(categoryRepository).findAllUniqueCategories();
    }
}