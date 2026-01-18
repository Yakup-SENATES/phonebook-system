package com.phonebook_system.report_service.model.exception;

import java.util.UUID;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(UUID reportId) {
        super("Report not found with id: " + reportId);
    }
}
