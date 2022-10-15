package com.example.domain.exceptions;

import org.springframework.validation.Errors;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class ValidationException  extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    public ValidationException() {
        super(TYPE, "erreur", Status.BAD_REQUEST);
    }
}
