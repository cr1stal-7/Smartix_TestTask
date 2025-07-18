package com.example.testTask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Рейтинг товара")
public class RatingDTO {

    @Schema(description = "Средняя оценка товара", example = "3.9")
    private Double rate;

    @Schema(description = "Количество товара", example = "120")
    private Integer count;
}