package com.example.features.accueil.domain.services;

import com.example.features.accueil.application.dto.OAuth2Provider;
import com.example.features.accueil.application.dto.OAuth2UserInfoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuth2Service {

    private final Map<String, OAuth2Provider> providers;

    public OAuth2Service(List<OAuth2Provider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuth2Provider::getName, p -> p));
    }

    public String getRedirectUrl(String providerName, String redirectUri) {
        return getProvider(providerName).getAuthorizationUrl(redirectUri);
    }

    public OAuth2UserInfoDTO processOAuth2Callback(String providerName, String code) {
        OAuth2Provider provider = getProvider(providerName);
        String token = provider.getAccessToken(code);
        return provider.getUserInfo(token);
    }

    private OAuth2Provider getProvider(String name) {
        OAuth2Provider provider = providers.get(name.toLowerCase());
        if (provider == null) throw new IllegalArgumentException("Provider non supporté : " + name);
        return provider;
    }

}
