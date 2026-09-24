package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.AddressRequest;
import com.example.gaming_ecomerce.dto.response.AddressResponse;
import com.example.gaming_ecomerce.model.Address;
import com.example.gaming_ecomerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/addresses")
    public List<AddressResponse> getAllAddresses() {
        return addressService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {
        return addressService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clients/{clientId}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddressesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(addressService.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping("/clients/{clientId}/addresses")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Long clientId, @Valid @RequestBody AddressRequest request) {
        try {
            Address address = new Address();
            address.setName(request.getName());
            address.setAddress(request.getAddress());
            address.setCity(request.getCity());
            address.setState(request.getState());
            address.setPostalCode(request.getPostalCode());
            address.setPhone(request.getPhone());
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(addressService.save(clientId, address)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        try {
            Address address = new Address();
            address.setName(request.getName());
            address.setAddress(request.getAddress());
            address.setCity(request.getCity());
            address.setState(request.getState());
            address.setPostalCode(request.getPostalCode());
            address.setPhone(request.getPhone());
            return ResponseEntity.ok(toResponse(addressService.update(id, address)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        if (addressService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        addressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getName(),
                address.getAddress(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getPhone(),
                address.getClient() != null ? address.getClient().getId() : null
        );
    }
}
