/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.component;

import com.example.domain.exceptions.AuthenticationException;
import com.example.domain.exceptions.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.ServletException;
import javax.validation.ConstraintDeclarationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Joel NOUMIA
 */
@ControllerAdvice
public class ApiHandleException extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiHandleException.class);

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Object> handleAuthentication(ServletException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleCredential(AuthenticationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(ConstraintDeclarationException.class)
    public ResponseEntity<Object> handleValidationDto(ConstraintDeclarationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.isNotFoundType()) {
            status = HttpStatus.NOT_FOUND;
            body.put("status", status);
        }
        if (ex.isOtherType()) {
            status = HttpStatus.BAD_REQUEST;
            body.put("status", HttpStatus.BAD_REQUEST);
        }
        if (ex.isInternalErrorType()) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(body, status);
    }


}
