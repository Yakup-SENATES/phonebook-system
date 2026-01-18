package com.phonebook_system.contact_service.model.exception;

import java.util.UUID;

public class ContactInfoNotFoundException extends RuntimeException {

    public ContactInfoNotFoundException(UUID personId) {
        super("ContactInfo not found with id: " + personId);
    }
}
