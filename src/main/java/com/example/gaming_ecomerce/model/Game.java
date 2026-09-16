package com.example.gaming_ecomerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String video;

    private String cover;

    private String wallpaper;

    @ElementCollection
    @CollectionTable(name = "game_screenshots", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "screenshot_url")
    private List<String> screenshots;

    private Integer discount;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    // RELACIONES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;
}
