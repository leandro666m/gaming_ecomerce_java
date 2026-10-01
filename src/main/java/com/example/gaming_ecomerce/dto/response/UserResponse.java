package com.example.gaming_ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class UserResponse {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean active;
    private String createdAt;
    private String updatedAt;

}
