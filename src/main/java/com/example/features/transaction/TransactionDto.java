package com.example.features.transaction;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {


    @Max(10000000)
    @NotNull(message = "Entrer un montant svp")
    private Integer montant;

    private LocalDate date;

    private Long bailId;

}
