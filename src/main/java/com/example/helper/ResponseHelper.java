package com.example.helper;

import com.example.domain.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public class ResponseHelper {

    /**
     * helper method use to build ResponseEntity
     *
     * @return {@link ResponseEntity}
     */
    public static ResponseEntity<Void> ok() {
        return ResponseEntity.ok().build();
    }

    /**
     * helper method use to build ResponseEntity
     *
     * @param object {@link Object}
     * @return {@link ResponseEntity}
     */
    public static <T> ResponseEntity<T> build(T object) {
        return object != null
                ? ResponseEntity.ok().body(object)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * handle errors
     *
     * @param errors {@link Errors}
     */
    public static void handle(Errors errors) throws ValidationException {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.getAllErrors());
        }
    }

}
