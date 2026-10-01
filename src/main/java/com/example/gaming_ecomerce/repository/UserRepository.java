package com.example.gaming_ecomerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.gaming_ecomerce.model.User;



@Repository 
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


}
