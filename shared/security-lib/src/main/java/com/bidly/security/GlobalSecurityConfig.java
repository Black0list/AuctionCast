package com.bidly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class GlobalSecurityConfig {

    private final KeycloakJwtConverter keycloakJwtConverter = new KeycloakJwtConverter();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Public Auth & Webhooks & Actuator
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/refresh").permitAll()
                        .requestMatchers("/stripe/webhook", "/stripe/webhook/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Products (Visitor/Seller/Admin)
                        .requestMatchers(HttpMethod.PUT, "/products/*/status/**").permitAll()
                        .requestMatchers("/products/my-products").hasRole("SELLER")
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("SELLER", "ADMIN")

                        // Categories (Visitor/Admin)
                        .requestMatchers(HttpMethod.GET, "/categories", "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        // Auctions (Visitor/Seller/Admin)
                        .requestMatchers(HttpMethod.GET, "/auctions/active", "/auctions/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auctions/{id:[a-fA-F0-9\\-]{36}}").permitAll()
                        .requestMatchers("/auctions/me").hasRole("SELLER")
                        .requestMatchers("/auctions/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/auctions/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/auctions/**").hasRole("SELLER")

                        // Bids & Wallet (Authenticated)
                        .requestMatchers("/bids/**").authenticated()
                        .requestMatchers("/wallet/**").authenticated()

                        // Orders (Visitor/Seller/Admin)
                        .requestMatchers("/orders/me/**").authenticated()
                        .requestMatchers("/orders/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/orders/*/ship").hasRole("SELLER")
                        .requestMatchers(HttpMethod.POST, "/orders/*/deliver").authenticated()

                        // Users (Visitor/Auth/Admin)
                        .requestMatchers(HttpMethod.GET, "/users/{userId}/is-seller").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/{userId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/batch-public-profiles").permitAll()
                        .requestMatchers("/users/apply-seller").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(keycloakJwtConverter);
        return converter;
    }
}