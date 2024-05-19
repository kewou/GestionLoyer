package com.example.filter;

import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
        if (httpServletRequest.getMethod().equals(HttpMethod.OPTIONS.name())
                || httpServletRequest.getRequestURI().equalsIgnoreCase("/beezyApi/authenticate") // Authentification
                || httpServletRequest.getRequestURI().equalsIgnoreCase("/beezyApi/users/create") // Inscription
                || httpServletRequest.getRequestURI().equalsIgnoreCase("/beezyApi/") // Page d'acceuil
                || httpServletRequest.getRequestURI().startsWith("/beezyApi/assets/")
                || httpServletRequest.getRequestURI().endsWith(".js")
                || httpServletRequest.getRequestURI().endsWith(".css")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (authorization == null || !authorization.trim().startsWith("Bearer ")) {
            sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT incorrect");
            return;
        } else {
            token = authorization.substring(7);
            try {
                userName = jwtUtils.getUsernameFromToken(token);
            } catch (Exception e) {
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT invalide");
                return;
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
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT invalide");
                return;
            }

        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
