package com.example.gaming_ecomerce.repository;

import com.example.gaming_ecomerce.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUsername(String username);

    Optional<Client> findByEmail(String email);
}