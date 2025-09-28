package com.example.features.bail;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.transaction.Transaction;
import com.example.features.user.domain.entities.Client;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Bail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appart_id")
    @JsonBackReference
    private Appart appart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "locataire_id")
    private Client locataire;

    @Column(nullable = false)
    private LocalDate dateEntree;

    @Column
    private LocalDate dateSortie;

    @Column
    private Boolean actif;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bail", orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    @JsonIgnore
    public Appart getAppart() {
        return this.appart;
    }

}
