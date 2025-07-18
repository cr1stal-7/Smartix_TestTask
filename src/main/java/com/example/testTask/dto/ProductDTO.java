package com.example.testTask.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String image;
    private String category;
    private RatingDTO rating;
}