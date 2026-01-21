package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import com.phonebook_system.report_service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportRetryService {

    private final ReportRepository reportRepository;
    private final ReportService reportService;

    //runs every 5 minutes to retry failed reports.
    @Scheduled(cron = "0 0/5 * * * *")
    public void retryFailedReports() {
        log.info("Starting retry mechanism for failed reports...");

        List<ReportEntity> failedReports = reportRepository.findByStatus(ReportStatus.FAILED);

        if (failedReports.isEmpty()) {
            log.info("No failed reports found to retry.");
            return;
        }

        log.info("Found {} failed reports. Processing...", failedReports.size());

        for (ReportEntity report : failedReports) {
            try {
                log.info("Retrying report with id: {}", report.getId());

                // Re-purpose the existing generation logic
                ReportRequestEvent event = ReportRequestEvent.builder()
                        .reportId(report.getId())
                        .requestDate(report.getRequestDate())
                        .build();

                reportService.generateReport(event);

            } catch (Exception e) {
                log.error("Error occurred while retrying report id: {}", report.getId(), e);
            }
        }

        log.info("Retry mechanism completed.");
    }
}
