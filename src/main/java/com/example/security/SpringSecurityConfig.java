package com.example.security;

import com.example.services.impl.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private SecurityProblemSupport problemSupport;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.exceptionHandling().authenticationEntryPoint(problemSupport).accessDeniedHandler(problemSupport);

        http
                .csrf().disable() // protocole de sécurité qui gère un token
                .authorizeRequests()
                    .antMatchers("/login*").permitAll() // Tout le monde a accès à cette page
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/proprio").hasAnyRole("ADMIN","PROPRIO")
                    .antMatchers("/user").hasAnyRole("ADMIN","USER")
                    //.anyRequest().authenticated()   // toutes les requetes doivent etre authentifiées
                    .and().formLogin().loginPage("/login.html");
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/configuration/ui", "/swagger-resources", "/configuration/ui",
                        "/swagger-resources/**", "/configuration/security", "/api-docs/swagger-config", "/swagger-ui/**", "/webjars/**")
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
