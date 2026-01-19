package com.phonebook_system.contact_service.config;

import com.phonebook_system.contact_service.base.ErrorResponse;
import com.phonebook_system.contact_service.model.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFound(
            PersonNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now()
                ));
    }
    @ExceptionHandler(PersonsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonsNotFound(
            PersonsNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(ContactInfoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContactInfoNotFound(
            ContactInfoNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now()
                ));
    }
    @ExceptionHandler(InvalidContactInfoException.class)
    public ResponseEntity<ErrorResponse> handleInvalidContactInfo(
            InvalidContactInfoException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(LocationStatsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLocationStatsNotFound(
            LocationStatsNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Unexpected error occurred" + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        Instant.now()
                ));
    }
}
