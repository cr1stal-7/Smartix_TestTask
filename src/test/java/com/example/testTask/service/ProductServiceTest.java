package com.example.testTask.service;

import com.example.testTask.dto.ProductDTO;
import com.example.testTask.dto.RatingDTO;
import com.example.testTask.model.Product;
import com.example.testTask.model.Category;
import com.example.testTask.model.Rating;
import com.example.testTask.repository.CategoryRepository;
import com.example.testTask.repository.ProductRepository;
import com.example.testTask.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Category category1;
    private Category category2;
    private Rating rating;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Electronics");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Clothing");

        rating = new Rating();
        rating.setId(1L);
        rating.setRate(4.5);
        rating.setCount(100);

        product1 = new Product();
        product1.setId(1L);
        product1.setTitle("Laptop");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setCategory(category1);
        product1.setRating(rating);

        product2 = new Product();
        product2.setId(2L);
        product2.setTitle("T-Shirt");
        product2.setPrice(new BigDecimal("19.99"));
        product2.setCategory(category2);
        product2.setRating(rating);

        productDTO = new ProductDTO();
        productDTO.setTitle("Phone");
        productDTO.setPrice(new BigDecimal("499.99"));
        productDTO.setCategory("Electronics");
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRate(4.0);
        ratingDTO.setCount(80);
        productDTO.setRating(ratingDTO);
    }

    /**
     * Проверяет корректность получения полного списка товаров с пагинацией.
     * Должен вернуть страницу со всеми товарами без фильтрации
     */
    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1, product2));
        when(productRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().containsAll(List.of(product1, product2)));
        verify(productRepository).findAll(pageable);
    }

    /**
     * Проверяет получение товара по существующему идентификатору.
     * Должен вернуть корректный объект товара, если он существует
     */
    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product result = productService.getProductById(1L);

        assertEquals(product1, result);
        verify(productRepository).findById(1L);
    }

    /**
     * Проверяет обработку случая запроса несуществующего товара.
     * Исключение, если товар с указанным id не найден
     */
    @Test
    void getProductById_WithNonExistingId_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
        verify(productRepository).findById(99L);
    }

    /**
     * Проверяет создание нового товара
     */
    @Test
    void createProduct_ShouldSaveNewProduct() {
        when(categoryRepository.findByName("Electronics")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.createProduct(productDTO);

        assertEquals("Phone", result.getTitle());
        assertEquals(new BigDecimal("499.99"), result.getPrice());
        assertEquals("Electronics", result.getCategory().getName());
        assertEquals(4.0, result.getRating().getRate());
        assertEquals(80, result.getRating().getCount());
        verify(productRepository).save(any(Product.class));
    }

    /**
     * Проверяет обновление данных существующего товара
     */
    @Test
    void updateProduct_WithExistingId_ShouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(categoryRepository.findByName("Electronics")).thenReturn(category1);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(1L, productDTO);

        assertEquals(1L, result.getId());
        assertEquals("Phone", result.getTitle());
        assertEquals(new BigDecimal("499.99"), result.getPrice());
        assertEquals("Electronics", result.getCategory().getName());
        verify(productRepository).save(any(Product.class));
    }

    /**
     * Проверяет фильтрацию товаров по ценовому диапазону
     */
    @Test
    void getProductsByPriceRange_ShouldReturnFilteredProducts() {
        BigDecimal minPrice = new BigDecimal("100");
        BigDecimal maxPrice = new BigDecimal("1000");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1));
        when(productRepository.findByPrice(minPrice, maxPrice, pageable)).thenReturn(expectedPage);

        Page<Product> result = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().contains(product1));
        assertFalse(result.getContent().contains(product2));
        verify(productRepository).findByPrice(minPrice, maxPrice, pageable);
    }

    /**
     * Проверяет попытку обновления несуществующего товара.
     * Исключение при попытке обновления товара с несуществующим id
     */
    @Test
    void updateProduct_WithNonExistingId_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(99L, productDTO));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }

    /**
     * Проверяет удаление существующего товара
     */
    @Test
    void deleteProduct_WithExistingId_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    /**
     * Проверяет попытку удаления несуществующего товара.
     * Исключение при попытке удаления товара с несуществующим id
     */
    @Test
    void deleteProduct_WithNonExistingId_ShouldThrowException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, never()).deleteById(any());
    }

    /**
     * Проверяет поведение при неопределенном ценовом диапазоне (оба параметра minPrice и maxPrice равны null).
     * Должен вернуть все товары, игнорируя фильтрацию по цене
     */
    @Test
    void getProductsByPriceRange_WithNullValues_ShouldReturnAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1, product2));
        when(productRepository.findByPrice(null, null, pageable)).thenReturn(expectedPage);

        Page<Product> result = productService.getProductsByPriceRange(null, null, pageable);

        assertEquals(2, result.getTotalElements());
        verify(productRepository).findByPrice(null, null, pageable);
    }

    /**
     * Проверяет фильтрацию товаров по категории.
     * Должен вернуть только товары, принадлежащие указанной категории
     */
    @Test
    void getProductsByCategory_ShouldReturnFilteredProducts() {
        String categoryName = "Electronics";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1));

        when(productRepository.findAllByCategoryName(categoryName, pageable)).thenReturn(expectedPage);

        Page<Product> result = productService.getProductsByCategory(categoryName, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(product1, result.getContent().get(0));
        verify(productRepository).findAllByCategoryName(categoryName, pageable);
    }

    /**
     * Проверяет сортировку товаров по убыванию цены
     */
    @Test
    void getProductsSorted_ByPriceDesc() {
        String priceDirection = "desc";
        Sort sort = Sort.by(Sort.Order.desc("price"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product1, product2)));

        Page<Product> result = productService.getProductsSorted(priceDirection, null, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().get(0).getPrice().compareTo(result.getContent().get(1).getPrice()) >= 0);
    }

    /**
     * Проверяет сортировку товаров по возрастанию цены
     */
    @Test
    void getProductsSorted_ByPriceAsc() {
        String priceDirection = "asc";
        Sort sort = Sort.by(Sort.Order.asc("price"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product2, product1)));

        Page<Product> result = productService.getProductsSorted(priceDirection, null, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().get(0).getPrice().compareTo(result.getContent().get(1).getPrice()) <= 0);
    }

    /**
     * Проверяет сортировку товаров по убыванию названия категории
     */
    @Test
    void getProductsSorted_ByCategoryDesc() {
        String categoryDirection = "desc";
        Sort sort = Sort.by(Sort.Order.desc("category.name"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product1, product2)));

        Page<Product> result = productService.getProductsSorted(null, categoryDirection, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().get(0).getCategory().getName().compareToIgnoreCase(result.getContent().get(1).getCategory().getName()) >= 0);
    }

    /**
     * Проверяет сортировку товаров по возрастанию названия категории
     */
    @Test
    void getProductsSorted_ByCategoryAsc() {
        String categoryDirection = "asc";
        Sort sort = Sort.by(Sort.Order.asc("category.name"));
        Pageable pageable = PageRequest.of(0, 10, sort);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product2, product1)));

        Page<Product> result = productService.getProductsSorted(null, categoryDirection, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().get(0).getCategory().getName().compareToIgnoreCase(result.getContent().get(1).getCategory().getName()) <= 0);
    }

    /**
     * Проверяет комбинированную сортировку по цене и категории одновременно
     */
    @Test
    void getProductsSorted_ByBothFields() {
        String priceDirection = "desc";
        String categoryDirection = "asc";
        Sort sort = Sort.by(
                Sort.Order.desc("price"),
                Sort.Order.asc("category.name")
        );
        Pageable pageable = PageRequest.of(0, 10, sort);
        Product product3 = new Product();
        product3.setPrice(new BigDecimal("999.99"));
        product3.setCategory(category2);
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(product1, product3, product2)));

        Page<Product> result = productService.getProductsSorted(priceDirection, categoryDirection, pageable);

        assertEquals(3, result.getTotalElements());
        assertTrue(result.getContent().get(0).getPrice().compareTo(result.getContent().get(1).getPrice()) >= 0);
        assertTrue(result.getContent().get(1).getPrice().compareTo(result.getContent().get(2).getPrice()) >= 0);
        assertEquals("Electronics", result.getContent().get(0).getCategory().getName());
        assertEquals("Clothing", result.getContent().get(1).getCategory().getName());
    }
}