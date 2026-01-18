package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportEventListener {
    private final ReportService reportService;


    @KafkaListener(topics = "report-requests", groupId = "report-group")
    public void handle(ReportRequestEvent event) {
        log.info("Received report request for id: {}", event.getReportId());
        reportService.generateReport(event);
    }
}
