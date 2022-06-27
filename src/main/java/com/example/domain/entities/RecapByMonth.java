package com.example.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "recapByMonth")
@Setter
@Getter
public class RecapByMonth implements Serializable {

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
    @JoinColumn(name = "logement_id",nullable = false)
    private Logement logement;

    @JsonIgnore
    public Logement getLogement() {
        return this.logement;
    }

    public RecapByMonth(){

    }

    public RecapByMonth(Date dateVersement, int montantLoyer, int montantVerser, int solde, Logement logement) {
        this.dateVersement = dateVersement;
        this.montantLoyer = montantLoyer;
        this.montantVerser = montantVerser;
        this.solde = solde;
        this.logement = logement;
    }
}
