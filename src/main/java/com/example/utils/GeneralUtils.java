package com.example.utils;

import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.Random;

public class GeneralUtils {

    @SneakyThrows
    public static String generateReference() {
        Random r = SecureRandom.getInstanceStrong();
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        return alphabet[r.nextInt(25)] + r.nextInt(9) + alphabet[r.nextInt(25)] + r.nextInt(9) + alphabet[r.nextInt(25)]
                + r.nextInt(9) + alphabet[r.nextInt(25)] + r.nextInt(9) + alphabet[r.nextInt(25)];
    }
}
