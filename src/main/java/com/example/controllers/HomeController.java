package com.example.controllers;

import com.example.domain.entities.Client;
import com.example.domain.exceptions.AuthenticationException;
import com.example.domain.jwt.JwtRequest;
import com.example.domain.jwt.JwtResponse;
import com.example.services.impl.AuthenticationService;
import com.example.services.impl.ClientService;
import com.example.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClientService clientService;


    @PostMapping("/authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("INVALID CREDENTIAL");
        }
        // Identifiants user + pass ok => On cree le token JWT
        /*
        final Client userDetails = authenticationService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtUtils.generateToken(userDetails);
        */
        Client client = clientService.getClientByEmail(jwtRequest.getUsername());
        final String token = jwtUtils.generateToken(client);
        return new JwtResponse(token);
    }

    @GetMapping("/locataire")
    public String homePrivee() {
        return "Welcome to your Perso Page Locataire";
    }
}
