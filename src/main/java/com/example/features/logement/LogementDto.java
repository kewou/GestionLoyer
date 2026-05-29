package com.example.features.logement;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogementDto {

    private String reference;

    @NotBlank(message = "Entrer un nom de quartier svp")
    private String quartier;

    @NotBlank(message = "Entrer un nom de ville svp")
    private String ville;

    @NotBlank(message = "Entrer une description svp")
    private String description;

    private Long totalEncaisse;

    private Long totalEndettement;
}


