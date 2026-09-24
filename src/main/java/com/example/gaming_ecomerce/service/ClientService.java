package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public Client update(Long id, Client updatedClient) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        client.setUsername(updatedClient.getUsername());
        client.setEmail(updatedClient.getEmail());
        client.setPassword(updatedClient.getPassword());
        client.setFirstName(updatedClient.getFirstName());
        client.setLastName(updatedClient.getLastName());

        return clientRepository.save(client);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }
}
