package com.example.features.user.application.mapper;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVirtualLocataireDto {

    @NotBlank(message = "Entrer un prenom svp")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "Entrer un nom svp")
    @Size(min = 2, max = 50)
    private String lastName;

    @Email(message = "Entrer une adresse email valide")
    private String email;

    private String phone;
}
