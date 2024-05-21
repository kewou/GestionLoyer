package com.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private String reference;

    @NotBlank(message = "Entrer un nom svp")
    @Size(min = 2, max = 10)
    private String name;

    @NotBlank(message = "Entrer un prénom svp")
    @Size(min = 2, max = 10)
    private String lastName;

    @NotBlank(message = "L'adresse email ne peut etre vide")
    @Email(message = "Entrer une adresse email valide")
    private String email;

    @NotBlank(message = "Entrer un numéro de téléphone")
    @Pattern(regexp = "(\\d)+")
    private String phone;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$")
    private String password;


}
