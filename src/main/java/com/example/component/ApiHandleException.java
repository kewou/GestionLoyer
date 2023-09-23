/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.component;

import com.example.domain.exceptions.AuthenticationProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * @author frup73532
 */
@ControllerAdvice
public class ApiHandleException implements ProblemHandling {

    private static Logger logger = LoggerFactory.getLogger(ApiHandleException.class);

    @ExceptionHandler(AuthenticationProblem.class)
    public ResponseEntity<Problem> handleAuthenticationProblem(AuthenticationProblem ex) {
        Problem problem = Problem.builder()
                .withStatus(Status.UNAUTHORIZED)
                .withDetail(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }


}
