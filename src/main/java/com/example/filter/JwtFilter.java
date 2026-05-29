package com.example.filter;

import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.utils.JWTUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final AuthenticationService authenticationService;

    public JwtFilter(JWTUtils jwtUtils, AuthenticationService authenticationService) {
        this.jwtUtils = jwtUtils;
        this.authenticationService = authenticationService;
    }

    private static final List<String> PUBLIC_PATHS = List.of(
            "/authenticate", "/oauth2/authorize/google", "/oauth2/callback/google",
            "/login", "/a-propos", "/user-roles", "/contact",
            "/users/verify-account", "/users/reset-password", "/users/update-password"
    );

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/users/create", "/locataire/users/create", "/assets/",
            "/swagger-ui", "/api-docs", "/actuator", "/payment/webhook"
    );

    private static final List<String> PUBLIC_EXTENSIONS = List.of(
            ".js", ".css", ".html", ".jpg", ".png"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) return true;
        if (path.equals("/") || path.isEmpty()) return true;
        if (PUBLIC_PATHS.stream().anyMatch(p -> p.equalsIgnoreCase(path))) return true;
        if (PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)) return true;
        if (PUBLIC_EXTENSIONS.stream().anyMatch(path::endsWith)) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String authorization = httpServletRequest.getHeader("Authorization");
        // Fallback : query param ?token= pour EventSource (SSE) qui ne supporte pas les headers custom
        if ((authorization == null || authorization.isBlank()) && httpServletRequest.getParameter("token") != null) {
            authorization = "Bearer " + httpServletRequest.getParameter("token");
        }
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


