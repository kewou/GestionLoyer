package com.example.features.accueil.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2UserInfoDTO {

    private String email;
    private String name;
    private String lastName;
    private String provider;
    private String pictureUrl;
}
