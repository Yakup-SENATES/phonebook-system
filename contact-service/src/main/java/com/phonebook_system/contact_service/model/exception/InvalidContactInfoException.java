package com.phonebook_system.contact_service.model.exception;


public class InvalidContactInfoException extends RuntimeException {

    public InvalidContactInfoException(String message ) {
        super(message);
    }
}
