package com.example.features.appart.application.mapper;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyerDto {

    private LocalDate mois;
    private int montantAttendu;
    private int montantVerse;
    private boolean ok;
    private boolean courant;
}

