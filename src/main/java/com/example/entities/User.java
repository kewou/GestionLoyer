/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.entities;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author frup73532
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable {

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

    @NotBlank(message="Entrer une adresse email svp")
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

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

}
