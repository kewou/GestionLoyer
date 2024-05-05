/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.domain.exceptions;

import javassist.NotFoundException;

import java.net.URI;

/**
 * @author frup73532
 */
public class NoClientFoundException extends NotFoundException {

    private static final URI TYPE = URI.create("https://example.org/not-found");

    public NoClientFoundException(Long userId) {
        super(String.format("No user found with this id '%s'", userId));
    }

    public NoClientFoundException(String reference) {
        super(String.format("No user found with this reference : '%s'", reference));
    }
}
