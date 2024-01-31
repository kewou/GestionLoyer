package com.example.domain.exceptions;

public class NoRecapFoundException extends Exception {

    public NoRecapFoundException(Long recapId) {
        super(String.format("No recap found with this id '%s'", recapId));
    }
}
