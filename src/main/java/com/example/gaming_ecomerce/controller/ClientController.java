package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.ClientRequest;
import com.example.gaming_ecomerce.dto.response.ClientResponse;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientResponse> getAllClients() {
        return clientService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        return clientService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ClientResponse> getClientByUsername(@PathVariable String username) {
        return clientService.findByUsername(username)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClientResponse> getClientByEmail(@PathVariable String email) {
        return clientService.findByEmail(email)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        Client client = new Client();
        client.setUsername(request.getUsername());
        client.setEmail(request.getEmail());
        client.setPassword(request.getPassword());
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(clientService.save(client)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        try {
            Client client = new Client();
            client.setUsername(request.getUsername());
            client.setEmail(request.getEmail());
            client.setPassword(request.getPassword());
            client.setFirstName(request.getFirstName());
            client.setLastName(request.getLastName());
            return ResponseEntity.ok(toResponse(clientService.update(id, client)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getUsername(),
                client.getEmail(),
                client.getFirstName(),
                client.getLastName()
        );
    }
}
