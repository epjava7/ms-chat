package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/chatbot/api/hotels", "/api/chatbot/api/hotels/**").permitAll()
            .requestMatchers("/api/chatbot/api/hotels/search-ai").permitAll()
            .requestMatchers("/api/chatbot/**").permitAll() //.hasRole("USER")
            .requestMatchers("/api/support/**").permitAll() //.hasRole("SUPPORT")
            .anyRequest().permitAll() //.authenticated()
)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt() 
            );
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3001")
                        .allowedMethods("*");
            }
        };
    }
}