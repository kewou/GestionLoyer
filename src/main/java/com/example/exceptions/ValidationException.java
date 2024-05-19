package com.example.exceptions;

import org.springframework.validation.ObjectError;

import javax.validation.ConstraintDeclarationException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ValidationException extends ConstraintDeclarationException {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    private final List<ObjectError> errors;

    public ValidationException() {
        super("DTO incorrect, veuillez vérifier le DTO");
        errors = null;
    }

    public ValidationException(List<ObjectError> errors) {
        this.errors = errors;
    }

    public List<String> errorCodes() {
        return Objects.requireNonNull(errors).stream()
                .map(ObjectError::getCodes)
                .filter(Objects::nonNull)
                .map(data -> data[0])
                .collect(Collectors.toList());
    }
}
