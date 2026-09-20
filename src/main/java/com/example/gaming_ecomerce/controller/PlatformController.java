package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.PlatformRequest;
import com.example.gaming_ecomerce.dto.response.PlatformResponse;
import com.example.gaming_ecomerce.model.Platform;
import com.example.gaming_ecomerce.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public List<PlatformResponse> getAllPlatforms() {
        return platformService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformResponse> getPlatformById(@PathVariable Long id) {
        return platformService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PlatformResponse> getPlatformBySlug(@PathVariable String slug) {
        return platformService.findBySlug(slug)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlatformResponse> createPlatform(@Valid @RequestBody PlatformRequest request) {
        Platform platform = new Platform();
        platform.setName(request.getName());
        platform.setSlug(request.getSlug());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(platformService.save(platform)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformResponse> updatePlatform(@PathVariable Long id, @Valid @RequestBody PlatformRequest request) {
        try {
            Platform platform = new Platform();
            platform.setName(request.getName());
            platform.setSlug(request.getSlug());
            return ResponseEntity.ok(toResponse(platformService.update(id, platform)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        if (platformService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        platformService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PlatformResponse toResponse(Platform platform) {
        return new PlatformResponse(
                platform.getId(),
                platform.getName(),
                platform.getSlug()
        );
    }
}
