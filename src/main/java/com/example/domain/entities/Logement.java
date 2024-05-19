package com.example.domain.entities;

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

    @Column(name = "address", unique = true)
    private String address;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logement", orphanRemoval = true)
    private Set<Appart> apparts = new HashSet<>();

    public Logement() {
    }

    public Logement(String address) {
        this.address = address;
    }


    @JsonIgnore
    public Client getClient() {
        return this.client;
    }


}
