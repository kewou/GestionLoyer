package com.example.features.appart.domain.entities;

import com.example.features.bail.Bail;
import com.example.features.logement.Logement;
import com.example.features.user.domain.entities.Client;
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

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prixLoyer")
    private Integer prixLoyer;

    @Column(name = "prixCaution")
    private Integer prixCaution;

    @ManyToOne
    @JoinColumn(name = "bailleur")
    private Client bailleur;

    @ManyToOne
    @JoinColumn(name = "logement", nullable = false)
    private Logement logement;

    @OneToMany(mappedBy = "appart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateEntree DESC")
    private Set<Bail> baux = new HashSet<>();

    @JsonIgnore
    public Client getBailleur() {
        return this.bailleur;
    }


    @JsonIgnore
    public Logement getLogement() {
        return this.logement;
    }


    @JsonIgnore
    public Set<Bail> getBaux() {
        return this.baux;
    }


}
