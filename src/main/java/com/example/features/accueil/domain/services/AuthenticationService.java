package com.example.features.accueil.domain.services;

import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    ClientService clientService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientService.getClientByEmail(URLDecoder.decode(username));
        if (client != null) {
            return client;
        } else {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
    }

}
