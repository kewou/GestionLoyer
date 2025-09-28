package com.example.features.logement;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.user.domain.entities.Client;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Logement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "quartier")
    private String quartier;

    @Column(name = "ville")
    private String ville;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logement", orphanRemoval = true)
    private Set<Appart> apparts = new HashSet<>();

    public Logement() {
    }

    public Logement(String quartier) {
        this.quartier = quartier;
    }


    @JsonIgnore
    public Client getClient() {
        return this.client;
    }


}
