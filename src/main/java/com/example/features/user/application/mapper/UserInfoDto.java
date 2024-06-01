package com.example.features.user.application.mapper;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    @NotBlank
    private String email;

    private String role;
}
