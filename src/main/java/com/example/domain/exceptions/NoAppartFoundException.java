package com.example.domain.exceptions;

import javassist.NotFoundException;

import java.net.URI;

public class NoAppartFoundException extends NotFoundException {
    private static final URI TYPE = URI.create("https://example.org/not-found");

    public NoAppartFoundException(Long appartId) {
        super(String.format("No appart found with this id '%s'", appartId));
    }

}
