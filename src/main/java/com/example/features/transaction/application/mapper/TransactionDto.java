package com.example.features.transaction.application.mapper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String reference;

    @Max(10000000)
    @NotNull(message = "Entrer un montant svp")
    private Integer montantVerser;

}
