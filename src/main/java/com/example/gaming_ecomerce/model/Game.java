package com.example.gaming_ecomerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String video;

    @Column(columnDefinition = "TEXT")
    private String cover;

    @Column(columnDefinition = "TEXT")
    private String wallpaper;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "game_screenshots", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "screenshot_url", columnDefinition = "TEXT")
    private List<String> screenshots = new ArrayList<>();

    private Integer discount;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @ManyToMany(mappedBy = "games", fetch = FetchType.LAZY)
    private List<Wishlist> wishlists = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}
