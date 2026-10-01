package com.example.gaming_ecomerce.config;

import com.example.gaming_ecomerce.service.DashboardUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrentUserAuthorizationFilter extends OncePerRequestFilter {

    private final DashboardUserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository;

    public CurrentUserAuthorizationFilter(
            DashboardUserDetailsService userDetailsService,
            SecurityContextRepository securityContextRepository) {
        this.userDetailsService = userDetailsService;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(authentication.getName());
        } catch (UsernameNotFoundException exception) {
            expireSession(request, response);
            return;
        }

        if (!user.isEnabled()) {
            expireSession(request, response);
            return;
        }

        Set<String> currentAuthorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());
        Set<String> updatedAuthorities = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        if (!currentAuthorities.equals(updatedAuthorities)) {
            UsernamePasswordAuthenticationToken updatedAuthentication =
                    UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
            updatedAuthentication.setDetails(authentication.getDetails());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(updatedAuthentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
        }

        filterChain.doFilter(request, response);
    }

    private void expireSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
