package com.example.controllers;

import com.example.domain.exceptions.AuthenticationException;
import com.example.domain.jwt.JwtRequest;
import com.example.domain.jwt.JwtResponse;
import com.example.services.impl.AuthenticationService;
import com.example.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping("/")
    public String home() {
        return "Welcome to Gestion Loyer Application";
    }

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
        final UserDetails userDetails = authenticationService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtUtils.generateToken(userDetails);

        return new JwtResponse(token);
    }

    @GetMapping("/locataire")
    public String homePrivee() {
        return "Welcome to your Perso Page Locataire";
    }
}
