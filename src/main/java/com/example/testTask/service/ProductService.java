package com.example.testTask.service;

import com.example.testTask.dto.ProductDTO;
import com.example.testTask.model.Product;
import com.example.testTask.model.Category;
import com.example.testTask.model.Rating;
import com.example.testTask.repository.CategoryRepository;
import com.example.testTask.repository.ProductRepository;
import com.example.testTask.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final RestClient restClient;

    /*
        Получение списка всех товаров
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /*
        Получение товара по id
        @param id идентификатор товара
        @throws RuntimeException если товар не найден
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    /*
        Создание нового товара
        @param productDto dto товара
     */
    public Product createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        return productRepository.save(product);
    }

    /*
        Редактирование существующего товара по id
        @param id идентификатор товара
        @param productDto dto товара
     */
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = getProductById(id);
        Product updatedProduct = convertToEntity(productDTO);
        updatedProduct.setId(existingProduct.getId());
        return productRepository.save(updatedProduct);
    }

    /*
        Удаление товара по id
        @param id идентификатор товара
        @throws RuntimeException если товар не найден
     */
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else throw new RuntimeException("Product not found with id: " + id);
    }

    /*
        Получение товаров в указанном ценовом диапазоне
        @param minPrice нижняя граница цены
        @param maxPrice верхняя граница цены
     */
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPrice(minPrice, maxPrice, pageable);
    }

    /*
        Получения товаров по категории
        @param categoryName наименование категории
     */
    public Page<Product> getProductsByCategory(String categoryName, Pageable pageable) {
        return productRepository.findAllByCategoryName(categoryName, pageable);
    }

    /*
        Получения списка отсортированных товаров
        @param priceDirection параметр сортировки по цене (asc/desc)
        @param categoryDirection параметр сортировки по категории (asc/desc)
     */
    public Page<Product> getProductsSorted(String priceDirection, String categoryDirection, Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        if (priceDirection != null) {
            orders.add(new Sort.Order(Sort.Direction.fromString(priceDirection), "price"));
        }
        if (categoryDirection != null) {
            orders.add(new Sort.Order(Sort.Direction.fromString(categoryDirection), "category.name"));
        }
        if (!orders.isEmpty()) {
            Sort sort = Sort.by(orders);
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        return productRepository.findAll(pageable);
    }

    /*
        Импорт товаров с внешнего api
        @Scheduled планировщик, синхронизирующий товары из внешнего api каждые 30 минут
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void importProducts() {
        ProductDTO[] products = restClient.get()
                .uri("/products")
                .retrieve()
                .body(ProductDTO[].class);

        if (products != null) {
            for (ProductDTO productDTO : products) {
                if (productDTO.getId() != null && productRepository.existsById(productDTO.getId())) {
                    updateProduct(productDTO.getId(), productDTO);
                } else {
                    createProduct(productDTO);
                }
            }
        }
    }

    /*
        Преобразование DTO товара в сущность Product
        @param dto dto товара для преобразования
     */
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setImage(dto.getImage());

        Category category = categoryRepository.findByName(dto.getCategory());
        if (category == null) {
            category = new Category();
            category.setName(dto.getCategory());
            category = categoryRepository.save(category);
        }
        product.setCategory(category);

        if (dto.getRating() != null) {
            Rating rating = new Rating();
            rating.setRate(dto.getRating().getRate());
            rating.setCount(dto.getRating().getCount());
            Rating savedRating = ratingRepository.save(rating);
            product.setRating(savedRating);
        }

        return product;
    }
}