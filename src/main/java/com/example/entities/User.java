/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.entities;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.Id;

import javax.persistence.*;

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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "entryDate")
    private Date entryDate;

    @Column(name = "getOutDate")
    private Date getOutDate;

    @Column(name = "solde")
    private int solde;

    @Column(name = "ancienneteEnMois")
    private int ancienneteEnMois;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private Set<Logement> logements = new HashSet<>();

    public User() {
    }

}
