/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.domain.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

/**
 * @author frup73532
 */
public class NoUserFoundProblem extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    public NoUserFoundProblem(Long userId) {
        super(TYPE, String.format("No user found with this id '%s'", userId), Status.NOT_FOUND);
    }
}
