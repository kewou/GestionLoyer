package com.example.features.accueil.application.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageCreateDto {

    @NotBlank(message = "Entrer votre nom")
    @Size(max = 50)
    String senderName;
    @NotBlank(message = "L'adresse email ne peut etre vide")
    @Email(message = "Entrer une adresse email valide")
    @Size(max = 150)
    String senderMail;
    @NotBlank(message = "Un message doit Ãªtre fourni")
    @Size(min = 10)
    String message;
}


