package com.example.features.transaction;

import com.example.features.bail.Bail;
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

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "montant")
    private Integer montant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bail_id")
    private Bail bail;


}
