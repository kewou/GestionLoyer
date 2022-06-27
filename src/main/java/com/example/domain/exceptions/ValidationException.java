package com.example.domain.exceptions;

import org.springframework.validation.Errors;

public class ValidationException extends RuntimeException{
    private final Errors errors;

    /**
     * constructor
     * @param message {@link String}
     * @param errors {@link Errors}
     */
    public ValidationException(String message, Errors errors)
    {
        super(message);
        this.errors = errors;
    }

    /**
     * get the errors
     * @return {@link Errors}
     */
    public Errors getErrors()
    {
        return errors;
    }
}
