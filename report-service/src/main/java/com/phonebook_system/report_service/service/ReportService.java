package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.base.BaseResponseModel;
import com.phonebook_system.report_service.client.ContactServiceClient;
import com.phonebook_system.report_service.entity.ReportDetailEntity;
import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.mapper.ReportMapper;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import com.phonebook_system.report_service.model.exception.InvalidReportStateException;
import com.phonebook_system.report_service.model.exception.ReportNotFoundException;
import com.phonebook_system.report_service.model.response.*;
import com.phonebook_system.report_service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper = ReportMapper.INSTANCE;
    private final ContactServiceClient contactServiceClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Transactional
    public ReportResponse requestReport() {
        ReportEntity report = ReportEntity.builder()
                .requestDate(LocalDateTime.now())
                .status(ReportStatus.PREPARING)
                .build();

        ReportEntity savedReport = reportRepository.save(report);

        UUID reportId = savedReport.getId();
        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(reportId)
                .requestDate(savedReport.getRequestDate())
                .build();

        kafkaTemplate.send(topic, reportId.toString(), event);

        return reportMapper.toResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportListResponse listReports() {
        List<ReportEntity> reports = reportRepository.findAll();
        List<ReportResponse> responseList = reportMapper.toResponseList(reports);
        return ReportListResponse.builder().reportList(responseList).build();
    }

    @Transactional(readOnly = true)
    public ReportDetailResponse getReportDetail(UUID id) {
        ReportEntity report = reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
        return reportMapper.toDetailResponse(report);
    }

    public void generateReport(ReportRequestEvent event) {
        log.info("Received report request for id: {}", event.getReportId());

        ReportEntity report = reportRepository.findById(event.getReportId())
                .orElseThrow(() -> new ReportNotFoundException(event.getReportId()));

        // Idempotency guard
        if (report.getStatus() == ReportStatus.COMPLETED) {
            return;
        }

        try {
            // Get stats from Contact Service
            BaseResponseModel<LocationStatisticListResponse> statsResponse =
                    contactServiceClient.getLocationStats();

            if (!statsResponse.isSuccess() || statsResponse.getData() == null) {
                log.error("Failed to get stats from Contact Service for report: {}", event.getReportId());
                throw new InvalidReportStateException("Failed to get statistics");
            }

            List<ReportDetailEntity> details =
                    statsResponse.getData().getLocationList().stream()
                    .map(stat -> ReportDetailEntity.builder()
                            .report(report)
                            .location(stat.getLocation())
                            .personCount(stat.getPersonCount())
                            .phoneNumberCount(stat.getPhoneNumberCount())
                            .build())
                    .toList();

            report.getDetails().clear();
            report.getDetails().addAll(details);
            report.setStatus(ReportStatus.COMPLETED);
            reportRepository.save(report);

            log.info("Report generated successfully for id: {}", event.getReportId());

        } catch (Exception e) {
            report.setStatus(ReportStatus.FAILED);
            reportRepository.save(report);
            log.error("Error generating report for id: {}", event.getReportId(), e);
        }
    }
}
