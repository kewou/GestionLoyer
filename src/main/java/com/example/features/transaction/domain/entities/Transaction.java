package com.example.features.transaction.domain.entities;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.user.domain.entities.Client;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "client_id_bailleur")
    private Client bailleur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appart_id")
    private Appart appart;

    @Column(name = "montantVerser")
    private Integer montantVerser;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "nbLoyerPayer")
    private Integer nbLoyerPayer;


}
