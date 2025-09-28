package com.example.features.bail.dto;

import com.example.features.user.application.mapper.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BailDto {

    private Long id;
    private ClientDto locataire;
    private LocalDate dateEntree;
    private LocalDate dateSortie;
    private Boolean actif;
}
