package com.mvc.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/styles/**", "/script/**", "/images/**").permitAll()
                        .requestMatchers("/products/**", "/register", "/forgot-password", "/products-details/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                ).formLogin(formLogin ->
                        formLogin.
                                loginPage("/login").
                                loginProcessingUrl("/login-process").
                                defaultSuccessUrl("/products").
                                permitAll()
                ).logout(logout ->
                        logout
                                .logoutSuccessUrl("/products")
                                .permitAll()

                )
        ;
        return http.build();
    }
}
