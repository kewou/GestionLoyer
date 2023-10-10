package com.example.services.impl;

import com.example.domain.entities.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    ClientService clientService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientService.getClientByEmail(username);
        if (client != null) {
            return client;
        } else {
            throw new UsernameNotFoundException("User" + username + "not found");
        }
    }

}
