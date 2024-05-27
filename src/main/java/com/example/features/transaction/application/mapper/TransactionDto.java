package com.example.features.transaction.application.mapper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String reference;

    @NotNull(message = "Entrer un montant svp")
    private Integer montantVerser;

}