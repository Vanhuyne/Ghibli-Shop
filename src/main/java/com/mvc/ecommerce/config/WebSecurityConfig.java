package com.mvc.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/styles/**", "/script/**").permitAll()
                            .requestMatchers("/products**", "/login", "register", "/login-process").permitAll()
                            .requestMatchers("/admin/**").hasAuthority("ADMIN")
                            .anyRequest().authenticated();

                });
        return http.build();
    }
}
