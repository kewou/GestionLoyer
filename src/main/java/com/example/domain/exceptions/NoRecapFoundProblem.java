package com.example.domain.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class NoRecapFoundProblem extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    public NoRecapFoundProblem(Long recapId) {
        super(TYPE, "Not found", Status.NOT_FOUND, String.format("No recap found with this id '%s'", recapId));
    }
}
