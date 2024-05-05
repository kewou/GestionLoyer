package com.example.domain.exceptions;

import javassist.NotFoundException;

import java.net.URI;

public class NoLogementFoundException extends NotFoundException {
    private static final URI TYPE = URI.create("https://example.org/not-found");

    public NoLogementFoundException(Long logementId) {
        super(String.format("No logement found with this id '%s'", logementId));
    }
}
