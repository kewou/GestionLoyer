/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * @author frup73532
 */
@ControllerAdvice
public class ApiHandleException implements ProblemHandling {

    private static Logger logger = LoggerFactory.getLogger(ApiHandleException.class);

}
