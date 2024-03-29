package com.example.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
public class Logement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "montantLoyer")
    private Integer montantLoyer;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "client", nullable = false)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "logement", orphanRemoval = true)
    private Set<RecapByMonth> recapByMonths;

    public Logement() {
    }

    public Logement(int montantLoyer, String address, String description, Client client) {
        this.montantLoyer = montantLoyer;
        this.address = address;
        this.description = description;
        this.client = client;
    }

    @JsonIgnore
    public Client getUser() {
        return this.client;
    }


}
