package com.example.features.bail.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class CreateBailRequestDto {
    @NotBlank
    private String locataireRef;

    @NotNull
    private LocalDate dateEntree;

    private LocalDate dateSortiePrevue;
}


