package com.example.features.accueil.application.dto;

public interface OAuth2Provider {

    String getAuthorizationUrl(String redirectUri);

    String getAccessToken(String code);

    OAuth2UserInfoDTO getUserInfo(String accessToken);

    String getName();
}
