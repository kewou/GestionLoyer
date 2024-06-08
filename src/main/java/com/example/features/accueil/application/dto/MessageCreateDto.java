package com.example.features.accueil.application.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
    String senderMail;
    @NotBlank(message = "Un message doit être fourni")
    @Size(min = 10)
    String message;
}
