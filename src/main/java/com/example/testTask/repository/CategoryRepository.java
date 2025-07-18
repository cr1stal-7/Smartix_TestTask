package com.example.testTask.repository;

import com.example.testTask.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<Category> findAllUniqueCategories();
}