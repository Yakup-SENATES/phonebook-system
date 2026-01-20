package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportEventListenerTest {
    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportEventListener reportEventListener;

    @Test
    void handle_ShouldCallGenerateReport() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(reportId)
                .requestDate(LocalDateTime.now())
                .build();

        // Act
        reportEventListener.handle(event);

        // Assert
        verify(reportService, times(1)).generateReport(event);
    }

}