package com.example.features.accueil.domain.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter(value = AccessLevel.NONE)
@Entity
public class Visiteur {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(name = "nbTotal")
    private Long nbTotal;
}



