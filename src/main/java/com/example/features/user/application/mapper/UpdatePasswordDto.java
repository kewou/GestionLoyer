package com.example.features.user.application.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UpdatePasswordDto {
    String email;
    String password;
    String verificationToken;
}
