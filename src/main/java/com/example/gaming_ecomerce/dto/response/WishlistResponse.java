package com.example.gaming_ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {

    private Long id;
    private Long userId;
    private List<Long> gameIds;
}
