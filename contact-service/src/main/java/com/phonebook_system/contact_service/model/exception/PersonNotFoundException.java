package com.phonebook_system.contact_service.model.exception;

import java.util.UUID;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(UUID personId) {
        super("Person not found with id: " + personId);
    }
}
