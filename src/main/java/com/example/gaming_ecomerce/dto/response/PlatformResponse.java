package com.example.gaming_ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformResponse {

    private Long id;
    private String name;
    private String slug;
}
