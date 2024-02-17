package com.mvc.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @Column(name = "username")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotEmpty(message = "Address cannot be empty")
    @Column(name = "address")
    private String address;

    @Column(name = "password")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotEmpty(message = "Full name cannot be empty")
    @Column(name = "fullname")
    private String fullname;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Column(name = "email")
    private String email;

    @Column(name = "photo")
    private String photo;

    @Column(name = "activated")
    private Boolean activated;

    @Column(name = "admin")
    private Boolean admin;
    
}
