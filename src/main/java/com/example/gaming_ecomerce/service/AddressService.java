package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Address;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.repository.AddressRepository;
import com.example.gaming_ecomerce.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;

    public AddressService(AddressRepository addressRepository, ClientRepository clientRepository) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public List<Address> findByClientId(Long clientId) {
        return addressRepository.findByClientId(clientId);
    }

    @Transactional
    public Address save(Long clientId, Address address) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clientId));
        address.setClient(client);
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
