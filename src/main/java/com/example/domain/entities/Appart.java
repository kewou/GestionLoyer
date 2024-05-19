package com.example.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Appart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nom", unique = true)
    private String nom;

    @Column(name = "prixLoyer")
    private Integer prixLoyer;

    @Column(name = "prixCaution")
    private Integer prixCaution;

    @ManyToOne
    @JoinColumn(name = "bailleur")
    private Client bailleur;

    @ManyToOne
    @JoinColumn(name = "locataire")
    private Client locataire;

    @ManyToOne
    @JoinColumn(name = "logement", nullable = false)
    private Logement logement;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appart", orphanRemoval = true)
    private Set<Loyer> loyers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appart", orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    @JsonIgnore
    public Client getBailleur() {
        return this.bailleur;
    }

    @JsonIgnore
    public Client getLocataire() {
        return this.locataire;
    }

    @JsonIgnore
    public Logement getLogement() {
        return this.logement;
    }
}
