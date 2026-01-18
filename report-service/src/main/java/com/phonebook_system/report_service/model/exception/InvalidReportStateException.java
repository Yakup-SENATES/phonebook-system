package com.phonebook_system.report_service.model.exception;


public class InvalidReportStateException extends RuntimeException {

    public InvalidReportStateException(String message) {
        super(message);
    }
}
