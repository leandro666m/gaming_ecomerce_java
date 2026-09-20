package com.example.gaming_ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String phone;
    private Long userId;
}
