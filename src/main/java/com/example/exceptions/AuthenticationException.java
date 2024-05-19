package com.example.exceptions;

public class AuthenticationException extends Exception {

    public AuthenticationException(String detail) {
        super(detail);
    }
}
