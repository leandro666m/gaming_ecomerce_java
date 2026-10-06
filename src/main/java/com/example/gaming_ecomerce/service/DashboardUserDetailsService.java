package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.repository.ClientRepository;
import com.example.gaming_ecomerce.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public DashboardUserDetailsService(UserRepository userRepository, ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            String authority = "admin".equalsIgnoreCase(user.getRole()) ? "ROLE_ADMIN" : "ROLE_USER";
            return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authority)
                .disabled(!user.isActive())
                .build();
        }

        Client client = clientRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        return org.springframework.security.core.userdetails.User.withUsername(client.getEmail())
            .password(client.getPassword())
            .authorities("ROLE_USER")
            .build();
    }

    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
            return loadUserByUsername(user.getEmail());
        }

        Client client = clientRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        client.setPassword(newPassword);
        clientRepository.save(client);
        return loadUserByUsername(client.getEmail());
    }
}
