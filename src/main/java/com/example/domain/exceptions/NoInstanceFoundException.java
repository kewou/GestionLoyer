/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.domain.exceptions;

/**
 * @author frup73532
 */
public class NoInstanceFoundException extends RuntimeException {

    public NoInstanceFoundException(String message) {
        super(message);
    }

    public NoInstanceFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
