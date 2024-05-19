package com.example.filter;

import com.example.exceptions.BusinessException;
import com.example.features.logement.domain.services.LogementService;
import com.example.features.user.domain.services.impl.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ClientPreFilter extends OncePerRequestFilter {

    @Autowired
    ClientService clientService;

    @Autowired
    LogementService logementService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (httpServletRequest.getRequestURI().matches("(?s).*\\/users\\/(.)+\\/logements(\\/\\d+|.*)")) {
            String pathInfo = httpServletRequest.getRequestURI();
            String[] parts = pathInfo.split("/");
            String reference = parts[3];
            try {
                clientService.getClientByReference(reference);
            } catch (BusinessException e) {
                sendErrorResponse(httpServletResponse, e.getMessage());
            }
        }

        if (httpServletRequest.getRequestURI().matches("(?s).*\\/users\\/(.)+\\/logements\\/(\\d+)\\/apparts(\\/\\d+|.*)")) {
            String pathInfo = httpServletRequest.getRequestURI();
            String[] parts = pathInfo.split("/");
            String reference = parts[3];
            String logementRef = parts[5];
            try {
                clientService.getClientByReference(reference);
                logementService.getLogementByReference(logementRef);
            } catch (BusinessException e) {
                sendErrorResponse(httpServletResponse, e.getMessage());
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setContentType("application/json");
        final String formattedResponse = String.format("{\"status\": \"%s\", \"timestamp\": \"%s\", \"message\": \"%s\"}",
                HttpStatus.NOT_FOUND, LocalDateTime.now(), errorMessage);
        response.getWriter().write(formattedResponse);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
