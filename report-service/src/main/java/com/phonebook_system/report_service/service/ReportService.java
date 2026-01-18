package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.base.BaseResponseModel;
import com.phonebook_system.report_service.client.ContactServiceClient;
import com.phonebook_system.report_service.entity.ReportDetailEntity;
import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.mapper.ReportMapper;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import com.phonebook_system.report_service.model.response.*;
import com.phonebook_system.report_service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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
    private final ReportMapper reportMapper;
    private final ContactServiceClient contactServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public BaseResponseModel<ReportResponse> requestReport() {
        ReportEntity report = ReportEntity.builder()
                .requestDate(LocalDateTime.now())
                .status(ReportStatus.PREPARING)
                .build();

        ReportEntity savedReport = reportRepository.save(report);

        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(savedReport.getId())
                .requestDate(savedReport.getRequestDate())
                .build();

        kafkaTemplate.send("report-requests", event);

        return BaseResponseModel.success(reportMapper.toResponse(savedReport));
    }

    @Transactional(readOnly = true)
    public BaseResponseModel<ReportListResponse> listReports() {
        List<ReportEntity> reports = reportRepository.findAll();
        List<ReportResponse> responseList = reportMapper.toResponseList(reports);
        ReportListResponse response = ReportListResponse.builder().reportList(responseList).build();
        return BaseResponseModel.success(response);
    }

    @Transactional(readOnly = true)
    public BaseResponseModel<ReportDetailResponse> getReportDetail(UUID id) {
        ReportEntity report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        return BaseResponseModel.success(reportMapper.toDetailResponse(report));
    }

    @KafkaListener(topics = "report-requests", groupId = "report-group")
    public void generateReport(ReportRequestEvent event) {
        log.info("Received report request for id: {}", event.getReportId());

        try {
            // Get stats from Contact Service
            BaseResponseModel<LocationStatisticListResponse> statsResponse = contactServiceClient.getLocationStats();

            if (statsResponse.isSuccess() && statsResponse.getData() != null) {
                ReportEntity report = reportRepository.findById(event.getReportId())
                        .orElseThrow(() -> new RuntimeException("Report not found"));

                List<ReportDetailEntity> details = statsResponse.getData().getLocationList().stream()
                        .map(stat -> ReportDetailEntity.builder()
                                .report(report)
                                .location(stat.getLocation())
                                .personCount(stat.getPersonCount())
                                .phoneNumberCount(stat.getPhoneNumberCount())
                                .build())
                        .toList();

                report.getDetails().addAll(details);
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);

                log.info("Report generated successfully for id: {}", event.getReportId());
            } else {
                log.error("Failed to get stats from Contact Service for report: {}", event.getReportId());
            }
        } catch (Exception e) {
            log.error("Error generating report for id: {}", event.getReportId(), e);
        }
    }
}
