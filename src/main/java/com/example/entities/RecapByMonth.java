package com.example.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "recapByMonth")
@Data
public class RecapByMonth {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @javax.persistence.Id
    private Long id;

    @Column(name = "dateVersement")
    private Date dateVersement;

    @Column(name = "montantLoyer")
    private int montantLoyer;

    @Column(name = "montantVerser")
    private int montantVerser;

    @Column(name = "solde")
    private int solde;

    @ManyToOne
    @JoinColumn(name = "logement_id", nullable = false)
    private Logement logement;
}
