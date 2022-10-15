package com.example.domain.dto;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

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
    private String phone;

    @NotBlank(message = "Entrer un nom password")
    private String password;


}
