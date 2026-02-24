package com.example.features.user.application.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private String reference;

    @NotBlank(message = "Entrer un prÃ©nom svp")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "Entrer un nom svp")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "L'adresse email ne peut etre vide")
    @Email(message = "Entrer une adresse email valide")
    private String email;

    private String phone;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$", message = "Le mot de passe doit contenir au moins 8 caractÃ¨res, dont une majuscule, un chiffre et un caractÃ¨re spÃ©cial")
    private String password;

    public ClientDto(String name, String lastName, String email, String phone) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }


}


