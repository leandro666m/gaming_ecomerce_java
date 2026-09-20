package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Address;
import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.repository.AddressRepository;
import com.example.gaming_ecomerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public List<Address> findByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public Address save(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
        address.setUser(user);
        return addressRepository.save(address);
    }

    @Transactional
    public Address update(Long id, Address updatedAddress) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con id: " + id));

        address.setName(updatedAddress.getName());
        address.setAddress(updatedAddress.getAddress());
        address.setCity(updatedAddress.getCity());
        address.setState(updatedAddress.getState());
        address.setPostalCode(updatedAddress.getPostalCode());
        address.setPhone(updatedAddress.getPhone());

        return addressRepository.save(address);
    }

    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }
}
