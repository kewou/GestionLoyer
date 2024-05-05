package com.example.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loyer", uniqueConstraints = {@UniqueConstraint(columnNames = {"dateLoyer", "appart"})})
@Getter
@Setter
/*
 * Chaque objet Loyer corespond à un mois de loyer de l'appart depuis sa création (mis à jour via des transactions)
 */
public class Loyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dateLoyer")
    private LocalDate dateLoyer;  // Permet de définir le mois du loyer en question

    @ManyToOne
    @JoinColumn(name = "appart", nullable = false)
    private Appart appart;

    @Column(name = "isOk")
    private Boolean isOk = false;

    @Column(name = "solde")
    private Integer solde;

    public Loyer(Appart appart) {  // La création d'un loyer se fait avec un solde dans le négatif
        this.appart = appart;
        this.solde = appart.getPrixLoyer() * -1;
    }

    public Loyer() {

    }

    public boolean isOk() {
        return isOk.booleanValue();
    }

    @JsonIgnore
    public Appart getAppart() {
        return this.appart;
    }
}
