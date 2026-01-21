package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import com.phonebook_system.report_service.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportRetryServiceTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    ReportService reportService;

    @InjectMocks
    ReportRetryService reportRetryService;

    @Test
    void retryFailedReports_FoundFailedReports_CallsGenerateReport() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportEntity report = ReportEntity.builder()
                .id(reportId)
                .requestDate(LocalDateTime.now())
                .status(ReportStatus.FAILED)
                .build();

        when(reportRepository.findByStatus(ReportStatus.FAILED)).thenReturn(List.of(report));

        // Act
        reportRetryService.retryFailedReports();

        // Assert
        verify(reportRepository).findByStatus(ReportStatus.FAILED);
        verify(reportService).generateReport(any(ReportRequestEvent.class));
    }

    @Test
    void retryFailedReports_NoFailedReports_DoNothing() {
        // Arrange
        when(reportRepository.findByStatus(ReportStatus.FAILED)).thenReturn(Collections.emptyList());

        // Act
        reportRetryService.retryFailedReports();

        // Assert
        verify(reportRepository).findByStatus(ReportStatus.FAILED);
        verify(reportService, never()).generateReport(any());
    }

    @Test
    void retryFailedReports_ExceptionDuringProcessing_ContinuesToNextReport() {
        // Arrange
        ReportEntity report1 = ReportEntity.builder().id(UUID.randomUUID()).build();
        ReportEntity report2 = ReportEntity.builder().id(UUID.randomUUID()).build();

        when(reportRepository.findByStatus(ReportStatus.FAILED)).thenReturn(List.of(report1, report2));
        doThrow(new RuntimeException("Simulated Error")).when(reportService)
                .generateReport(argThat(event -> event.getReportId().equals(report1.getId())));

        // Act
        reportRetryService.retryFailedReports();

        // Assert
        verify(reportService, times(2)).generateReport(any());
    }
}
