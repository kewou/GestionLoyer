package com.example.domain.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class AuthenticationProblem extends AbstractThrowableProblem {

    public AuthenticationProblem(String detail) {
        super(URI.create("/authentication-failed"), "Authentication failed", Status.UNAUTHORIZED, detail);
    }
}
