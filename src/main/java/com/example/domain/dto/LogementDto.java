package com.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogementDto {

    private Long id;

    @NotBlank(message = "Entrer une adresse svp")
    private String address;

    @NotBlank(message = "Entrer une description svp")
    private String description;
}
