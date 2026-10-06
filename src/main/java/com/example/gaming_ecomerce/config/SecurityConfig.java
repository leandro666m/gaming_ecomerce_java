package com.example.gaming_ecomerce.config;

import com.example.gaming_ecomerce.service.DashboardUserDetailsService;
import com.example.gaming_ecomerce.service.LegacyAwarePasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            CsrfTokenRepository csrfTokenRepository,
            SecurityContextRepository securityContextRepository,
            CorsConfigurationSource corsConfigurationSource,
            CurrentUserAuthorizationFilter currentUserAuthorizationFilter) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(request -> !requiresCsrf(request.getMethod(), request.getRequestURI())))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .sessionManagement(session -> session.sessionFixation(fixation -> fixation.changeSessionId()))
                .addFilterBefore(currentUserAuthorizationFilter,
                        org.springframework.security.web.access.intercept.AuthorizationFilter.class)
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/csrf", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").authenticated()
                        .requestMatchers("/api/checkout/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/clients").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/clients").hasRole("ADMIN")
                    .requestMatchers("/api/clients/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/addresses", "/api/wishlists", "/api/orders", "/api/order-items").hasRole("ADMIN")
                    .requestMatchers("/api/addresses/**", "/api/wishlists/**", "/api/orders/**", "/api/order-items/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/games/*/order-items").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/games/**", "/api/platforms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/games/**", "/api/platforms/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            DashboardUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsPasswordService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new LegacyAwarePasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CurrentUserAuthorizationFilter currentUserAuthorizationFilter(
            DashboardUserDetailsService userDetailsService,
            SecurityContextRepository securityContextRepository) {
        return new CurrentUserAuthorizationFilter(userDetailsService, securityContextRepository);
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/");
        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:5174}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static boolean requiresCsrf(String method, String requestUri) {
        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method)
                || HttpMethod.OPTIONS.matches(method) || HttpMethod.TRACE.matches(method)) {
            return false;
        }

        return requestUri.startsWith("/api/auth/")
            || requestUri.startsWith("/api/clients")
            || requestUri.startsWith("/api/addresses")
            || requestUri.startsWith("/api/wishlists")
            || requestUri.startsWith("/api/orders")
            || requestUri.startsWith("/api/order-items")
                || requestUri.startsWith("/api/checkout/")
                || requestUri.equals("/api/users")
                || requestUri.startsWith("/api/users/")
                || requestUri.equals("/api/platforms")
                || requestUri.startsWith("/api/games/")
                || requestUri.startsWith("/api/platforms/");
    }
}
