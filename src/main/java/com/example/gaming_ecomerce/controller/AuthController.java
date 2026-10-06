package com.example.gaming_ecomerce.controller;

import com.example.gaming_ecomerce.dto.LoginRequest;
import com.example.gaming_ecomerce.dto.response.UserResponse;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.repository.ClientRepository;
import com.example.gaming_ecomerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final CsrfTokenRepository csrfTokenRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            CsrfTokenRepository csrfTokenRepository,
            UserRepository userRepository,
            ClientRepository clientRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.csrfTokenRepository = csrfTokenRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (servletRequest.getSession(false) != null) {
            servletRequest.changeSessionId();
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        csrfTokenRepository.saveToken(null, servletRequest, servletResponse);

        return ResponseEntity.ok(toResponse(authentication.getName()));
    }

    @GetMapping("/me")
    public UserResponse currentUser(Authentication authentication) {
        return toResponse(authentication.getName());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        csrfTokenRepository.saveToken(null, request, response);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return new UserResponse(
                    user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                    user.getRole(), user.isActive(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                    user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        }

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationServiceException("El usuario autenticado ya no existe."));
        return new UserResponse(
                client.getId(), client.getEmail(), client.getFirstName(), client.getLastName(),
                "CLIENT", true, null, null);
    }
}
