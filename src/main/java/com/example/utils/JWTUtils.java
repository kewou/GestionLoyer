package com.example.utils;

import com.example.features.user.domain.entities.UserDetailsCustom;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils implements Serializable {

    private static final long serialVersionUID = 234234523523L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 heures

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // retrieve reference from jwt token
    public String getReferenceFromToken(String token) {
        return getClaimFromToken(token, claims -> (String) claims.get("reference"));
    }

    // retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    // check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // generate token for user
    public String generateToken(UserDetailsCustom userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reference", userDetails.getReference());
        claims.put("roles", userDetails.getAuthorities());
        return doGenerateToken(claims, URLEncoder.encode(userDetails.getUsername(), StandardCharsets.UTF_8));
    }

    // while creating the token -
    // 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the secret key.
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(secretKey)
                .compact();
    }

    // validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = URLDecoder.decode(getUsernameFromToken(token), StandardCharsets.UTF_8);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}


