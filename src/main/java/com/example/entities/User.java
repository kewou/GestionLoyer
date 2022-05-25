/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.entities;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

/**
 * @author frup73532
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @javax.persistence.Id
    private Long id;

    @NotBlank(message = "Entrer un nom svp")
    @Size(min=2,max=10)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Entrer un prénom svp")
    @Size(min=2,max=10)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message="L'adresse email ne peut etre vide")
    @Email(message="Entrer une adresse email valide")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "entryDate")
    private Date entryDate;

    @Column(name = "getOutDate")
    private Date getOutDate;

    @Column(name = "solde")
    private int solde=0;

    @Column(name = "ancienneteEnMois")
    private int ancienneteEnMois=0;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private Set<Logement> logements;

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
