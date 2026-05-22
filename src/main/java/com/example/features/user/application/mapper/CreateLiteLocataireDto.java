package com.example.features.user.application.mapper;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLiteLocataireDto {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Le prénom est obligatoire")
    private String lastName;

    private String phone;
}

