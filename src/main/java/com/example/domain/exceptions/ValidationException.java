package com.example.domain.exceptions;

import javax.validation.ConstraintDeclarationException;
import java.net.URI;

public class ValidationException extends ConstraintDeclarationException {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    public ValidationException() {
        super("DTO incorrect, veuillez vérifier le DTO");
    }
}
