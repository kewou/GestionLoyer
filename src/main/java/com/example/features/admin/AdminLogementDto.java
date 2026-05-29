package com.example.features.admin;

import lombok.Data;

@Data
public class AdminLogementDto {
    private String reference;
    private String quartier;
    private String ville;
    private String description;
    private String bailleurName;
    private String bailleurReference;
}

