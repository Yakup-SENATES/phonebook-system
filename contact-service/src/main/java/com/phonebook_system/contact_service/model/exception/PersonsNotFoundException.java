package com.phonebook_system.contact_service.model.exception;


public class PersonsNotFoundException extends RuntimeException {

    public PersonsNotFoundException() {
        super("Persons are not found");
    }
}
