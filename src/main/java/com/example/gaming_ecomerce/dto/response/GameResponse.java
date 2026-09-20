package com.example.gaming_ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

    private Long id;
    private String title;
    private String slug;
    private BigDecimal price;
    private String summary;
    private String video;
    private String cover;
    private String wallpaper;
    private List<String> screenshots;
    private Integer discount;
    private LocalDate releaseDate;
    private Long platformId;
}
