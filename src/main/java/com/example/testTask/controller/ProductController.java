package com.example.testTask.controller;

import com.example.testTask.dto.ProductDTO;
import com.example.testTask.model.Product;
import com.example.testTask.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Tag(name = "Получение всех товаров")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Число элементов") int size) {
        return ResponseEntity.ok(productService.getAllProducts(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Tag(name = "Получение товара по id")
    public ResponseEntity<Product> getProductById(@PathVariable @Parameter(description = "Идентификатор товара") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @Tag(name = "Создание нового товара")
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PutMapping("/{id}")
    @Tag(name = "Редактирование существующего товара по id")
    public ResponseEntity<Product> updateProduct(
            @PathVariable @Parameter(description = "Идентификатор товара") Long id,
            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Удаление товара по id")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Parameter(description = "Идентификатор товара") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter-price")
    @Tag(name = "Фильтрация товаров по стоимости", description = "Возвращает список товаров в указанном ценовом диапазоне")
    public ResponseEntity<Page<Product>> filterByPriceRange(
            @RequestParam(required = false)  @Parameter(description = "Нижняя граница цены") BigDecimal minPrice,
            @RequestParam(required = false)  @Parameter(description = "Верхняя граница цены") BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Число элементов") int size) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice, PageRequest.of(page, size)));
    }

    @GetMapping("/category")
    @Tag(name = "Получение товаров по категории", description = "Возвращает список всех товаров с указанной категорией")
    public ResponseEntity<Page<Product>> getProductsByCategoryName(
            @RequestParam @Parameter(description = "Название категории") String categoryName,
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Число элементов") int size) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryName, PageRequest.of(page, size)));
    }

    @GetMapping("/sort")
    @Tag(name = "Сортировка товаров по категории и цене", description = "Сортировка товаров сразу по двум полям (цена и название категории) " +
                    "с указанием отдельного направления для каждого из этих полей (возрастание/убывание)")
    public ResponseEntity<Page<Product>> sortProducts(
            @RequestParam(required = false) @Parameter(description = "Направление сортировки 'asc'/'desc'") String priceDirection,
            @RequestParam(required = false) @Parameter(description = "Направление сортировки 'asc'/'desc'") String categoryDirection,
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Число элементов") int size) {
        return ResponseEntity.ok(productService.getProductsSorted(priceDirection, categoryDirection, PageRequest.of(page, size)));
    }

    @PostMapping("/import")
    @Tag(name = "Импорт данных с внешнего api (https://fakestoreapi.com/products)")
    public ResponseEntity<Void> importProducts() {
        productService.importProducts();
        return ResponseEntity.ok().build();
    }
}