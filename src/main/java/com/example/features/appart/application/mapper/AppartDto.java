package com.example.features.appart.application.mapper;

import com.example.features.loyer.domain.entities.Loyer;
import com.example.features.user.application.mapper.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppartDto {

    private String reference;

    @NotNull(message = "Entrer un nom pour votre logement")
    private String nom;

    @Max(1000000)
    @NotNull(message = "Entrer un montant svp")
    private Integer prixLoyer;

    @Max(1000000)
    @NotNull(message = "Entrer un montant svp")
    private Integer prixCaution;

    private ClientDto locataire;

    private Set<Loyer> loyers;


}
