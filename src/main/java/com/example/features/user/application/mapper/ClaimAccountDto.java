package com.example.features.user.application.mapper;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAccountDto {

    @NotBlank(message = "Le code d'invitation est requis")
    private String invitationCode;

    @NotBlank(message = "L'adresse email ne peut etre vide")
    @Email(message = "Entrer une adresse email valide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caracteres, dont une majuscule, un chiffre et un caractere special")
    private String password;

    private String phone;
}
