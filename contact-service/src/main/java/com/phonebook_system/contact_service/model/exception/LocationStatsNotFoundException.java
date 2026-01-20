package com.phonebook_system.contact_service.model.exception;


public class LocationStatsNotFoundException extends RuntimeException {

    public LocationStatsNotFoundException() {
        super("LocationStats not found");
    }
}
