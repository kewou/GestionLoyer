package com.example.security;

import com.example.services.impl.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationService authenticationService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // protocole de sécurité qui gère un token
                .authorizeRequests()
                    .antMatchers("/login*").permitAll() // Tout le monde a accès à cette page
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/proprio").hasRole("PROPRIO")
                    .antMatchers("/user").hasRole("USER")
                    .anyRequest().authenticated()    // toutes les requetes doivent etre authentifiées
                .and().formLogin();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/configuration/ui","/swagger-resources","/configuration/ui",
                        "/swagger-resources/**","/configuration/security","/api-docs/swagger-config","/swagger-ui/**","/webjars/**")
                .antMatchers("/users")
                .antMatchers("/swagger-ui-custom.html")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/api-docs")
                .antMatchers("/actuator");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticationService).passwordEncoder(new BCryptPasswordEncoder());
    }

}
