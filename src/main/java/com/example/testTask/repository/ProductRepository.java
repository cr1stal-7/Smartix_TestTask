package com.example.testTask.repository;

import com.example.testTask.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByPrice(@Param("minPrice")BigDecimal minPrice,
                              @Param("maxPrice")BigDecimal maxPrice,
                              Pageable pageable);

    Page<Product> findAllByCategoryName(String categoryName, Pageable pageable);
}