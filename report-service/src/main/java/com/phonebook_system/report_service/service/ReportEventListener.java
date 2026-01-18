package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import com.phonebook_system.report_service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportEventListener {
    private final ReportRepository reportRepository;


    @KafkaListener(topics = "report-requests")
    public void handle(ReportRequestEvent event) {
        ReportEntity report = ReportEntity.builder()
                .id(event.getReportId())
                .requestDate(event.getRequestDate())
                .status(ReportStatus.PREPARING)
                .build();

        reportRepository.save(report);
    }
}
