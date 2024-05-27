package com.example.security;

import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.filter.ClientPreFilter;
import com.example.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private ClientPreFilter clientPreFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticationService).passwordEncoder(passwordEncoder());
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // protocole de sécurité qui gère un token
                .authorizeRequests()
                .antMatchers("/", "/*.js", "/*.css", "/assets/**", "/users/create*", "/authenticate").permitAll() // Tout le monde a accès à cette page
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/admin").hasAuthority("ADMIN")
                .antMatchers("/bailleur").hasAnyAuthority("ADMIN", "BAILLEUR")
                .antMatchers("/locataire").hasAnyAuthority("ADMIN", "LOCATAIRE")
                .anyRequest().authenticated()   // toutes les requetes doivent etre authentifiées
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Ajout du Filtre
        http.addFilterAfter(clientPreFilter, JwtFilter.class);
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/configuration/ui", "/swagger-resources", "/configuration/ui",
                        "/swagger-resources/**", "/configuration/security", "/api-docs/swagger-config", "/swagger-ui/**", "/webjars/**",
                        "/swagger-ui-custom.html", "/swagger-ui.html", "/api-docs", "/actuator", "/index.html")
                .antMatchers();
    }

}
