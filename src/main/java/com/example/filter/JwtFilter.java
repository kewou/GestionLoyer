package com.example.filter;

import com.example.domain.exceptions.AuthenticationProblem;
import com.example.services.impl.AuthenticationService;
import com.example.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (httpServletRequest.getRequestURI().startsWith("/beezyApi/authenticate")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (null == authorization || !authorization.startsWith("Bearer ")) {
            throw new AuthenticationProblem("Token JWT incorrect");
        } else {
            token = authorization.substring(7);
            try {
                userName = jwtUtils.getUsernameFromToken(token);
            } catch (Exception e) {
                throw new AuthenticationProblem("Token JWT invalide");
            }
        }

        if (null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = authenticationService.loadUserByUsername(userName);
            if (jwtUtils.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                );

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                throw new AuthenticationProblem("Token JWT invalide");
            }

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
