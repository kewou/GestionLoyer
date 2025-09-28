package com.example.features.accueil.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.logement.Logement;
import com.example.features.logement.LogementAppService;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;

@Service
public class AuthenticationService implements UserDetailsService {


    private final ClientAppService clientAppService;
    private final LogementAppService logementAppService;
    private final AppartAppService appartAppService;

    @Autowired
    public AuthenticationService(ClientAppService clientAppService, LogementAppService logementAppService, AppartAppService appartAppService) {
        this.clientAppService = clientAppService;
        this.logementAppService = logementAppService;
        this.appartAppService = appartAppService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientAppService.getClientByEmail(URLDecoder.decode(username));
        if (client != null) {
            return client;
        } else {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
    }

    public String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public boolean isUserConnected(String refUser) throws BusinessException {
        Client client = clientAppService.getClientFromDatabase(refUser);
        String clientUsername = this.getLoggedInUsername();
        return clientUsername != null && clientUsername.equals(client.getUsername());
    }

    public boolean isOwnerLogement(String refUser, String refLgt) throws BusinessException {
        Client client = clientAppService.getClientFromDatabase(refUser);
        Logement lgt = logementAppService.getLogementFromDatabase(refLgt);
        return isUserConnected(refUser) && client.equals(lgt.getClient());
    }

    public boolean isOwnerAppart(String refLgt) throws BusinessException {
        Logement lgt = logementAppService.getLogementFromDatabase(refLgt);
        Client client = lgt.getClient();
        return isUserConnected(client.getReference()) && client.equals(lgt.getClient());
    }

    public boolean isOwnerBailleurAppart(String refUser, String refAppart) throws BusinessException {
        Client client = clientAppService.getClientFromDatabase(refUser);
        Appart appart = appartAppService.getAppartFromDatabase(refAppart);
        return isUserConnected(refUser) && client.equals(appart.getBailleur());
    }

/*
    public boolean isOwnerLcataireAppart(String refUser, String refAppart) throws BusinessException {
        Client client = clientAppService.getClientFromDatabase(refUser);
        Appart appart = appartAppService.getAppartFromDatabase(refAppart);
        return isUserConnected(refUser) && client.equals(appart.getB);
    }*/


}
