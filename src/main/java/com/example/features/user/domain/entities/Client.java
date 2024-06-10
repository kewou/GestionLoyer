/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.features.user.domain.entities;


import com.example.features.logement.domain.entities.Logement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sbeezy
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Client implements UserDetailsCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client", orphanRemoval = true)
    private Set<Logement> logements = new HashSet<>();

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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Ajoutez les rôles de l'utilisateur en tant qu'objets GrantedAuthority
        for (String role : this.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }

    public Client(String name) {
        this.name = name;
    }

    public Client() {

    }

    @Override
    public String getReference() {
        return this.reference;
    }
}
