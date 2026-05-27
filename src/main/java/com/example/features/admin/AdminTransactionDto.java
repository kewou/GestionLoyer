package com.example.features.admin;

import lombok.Data;

@Data
public class AdminTransactionDto {
    private Long id;
    private Integer montant;
    private String date;
    private Long bailId;
    private String appartNom;
    private String appartReference;
    private String locataireName;
    private String locataireReference;
}

