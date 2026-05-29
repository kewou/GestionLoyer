package com.example.features.admin;

import lombok.Data;

@Data
public class AdminBailDto {
    private Long id;
    private String locataireName;
    private String locataireReference;
    private String appartNom;
    private String appartReference;
    private String logementReference;
    private String dateEntree;
    private String dateSortie;
    private Boolean actif;
}

