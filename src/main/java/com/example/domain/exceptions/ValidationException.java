package com.example.domain.exceptions;

import java.net.URI;

public class ValidationException extends Exception {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    public ValidationException() {
        super("erreur");
    }
}
