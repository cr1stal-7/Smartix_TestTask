package com.example.testTask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Сущность товара")
public class ProductDTO {
    private Long id;

    @Schema(description = "Наименование товара", example = "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops")
    private String title;

    @Schema(description = "Цена товара", example = "109.95")
    private BigDecimal price;

    @Schema(description = "Описание товара", example = "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday")
    private String description;

    @Schema(description = "Url-адрес изображения", example = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg")
    private String image;

    @Schema(description = "Категория товара", example = "men's clothing")
    private String category;

    private RatingDTO rating;
}