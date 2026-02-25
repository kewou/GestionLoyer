package com.example.security;

import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.filter.ClientPreFilter;
import com.example.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private ClientPreFilter clientPreFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authenticationService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/assets/**", "/**/*.js", "/users/create*", "/authenticate", "/oauth2/**", "/login",
                                "/user-roles", "/contact",
                                "/a-propos", "/users/verify-account", "/users/reset-password", "/users/update-password",
                                "/swagger-ui/**", "/api-docs/**", "/actuator/**")
                        .permitAll()
                        .requestMatchers("/", "/index.html", "/static/**", "/js/**", "/css/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/bailleur/**").hasAnyAuthority(Role.ADMIN.name(), Role.BAILLEUR.name())
                        .requestMatchers("/locataire/**").hasAnyAuthority(Role.ADMIN.name(), Role.LOCATAIRE.name())
                        .requestMatchers("/locataires/**").hasAnyAuthority(Role.ADMIN.name(), Role.LOCATAIRE.name())
                        .requestMatchers("/bailleur/users/**").hasAnyAuthority(Role.ADMIN.name(), Role.BAILLEUR.name())
                        .requestMatchers("/admin/users/**").hasAuthority(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(clientPreFilter, JwtFilter.class);

        return http.build();
    }

}