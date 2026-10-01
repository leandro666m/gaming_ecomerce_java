package com.example.gaming_ecomerce.service;

import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class LegacyPasswordMigration implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LegacyPasswordMigration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<User> users = userRepository.findAll();
        List<User> migratedUsers = new ArrayList<>();

        for (User user : users) {
            String storedPassword = user.getPassword();
            if (storedPassword != null && passwordEncoder.upgradeEncoding(storedPassword)) {
                user.setPassword(passwordEncoder.encode(storedPassword));
                migratedUsers.add(user);
            }
        }

        userRepository.saveAll(migratedUsers);
    }
}
