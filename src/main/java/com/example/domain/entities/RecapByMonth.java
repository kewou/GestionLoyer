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

    @Column(name = "montantVerser")
    private Integer montantVerser;

    @Column(name = "solde")
    private Integer solde;

    @ManyToOne
    @JoinColumn(name = "logement_id",nullable = false)
    private Logement logement;

    @JsonIgnore
    public Logement getLogement() {
        return this.logement;
    }

    public RecapByMonth(){}


    public RecapByMonth(Date dateVersement, int montantVerser, int solde, Logement logement) {
        this.dateVersement = dateVersement;
        this.montantVerser = montantVerser;
        this.solde = solde;
        this.logement = logement;
    }
}
