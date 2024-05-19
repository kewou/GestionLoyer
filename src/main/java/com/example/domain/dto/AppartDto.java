package com.example.domain.dto;

import com.example.domain.entities.Loyer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppartDto {

    private Long id;

    @NotBlank(message = "Entrer un nom pour le distinguer")
    private String nom;

    @NotNull(message = "Entrer un montant svp")
    private int prixLoyer;

    @NotNull(message = "Entrer un montant svp")
    private int prixCaution;

    private ClientDto locataire;

    private Set<Loyer> loyers;


}
