package com.example.features.admin;

import lombok.Data;

@Data
public class AdminAppartDto {
    private String reference;
    private String nom;
    private Integer prixLoyer;
    private Integer prixCaution;
    private String logementReference;
    private String logementDescription;
    private String bailleurReference;
    private String bailleurName;
}

