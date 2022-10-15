package com.example.domain.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class NoLogementFoundProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://example.org/not-found");

    public NoLogementFoundProblem(Long logementId) {
        super(TYPE, "Not found", Status.NOT_FOUND, String.format("No logement found with this id '%s'", logementId));
    }
}
