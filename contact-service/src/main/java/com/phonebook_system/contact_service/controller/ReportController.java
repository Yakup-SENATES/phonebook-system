package com.phonebook_system.contact_service.controller;

import com.phonebook_system.contact_service.service.ReportRequestProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRequestProducer producer;

    @PostMapping
    public ResponseEntity<UUID> requestReport() {
        UUID reportId = producer.sendReportRequest();
        return ResponseEntity.accepted().body(reportId);
    }

}
