package com.phonebook_system.report_service.service;

import com.phonebook_system.report_service.base.BaseResponseModel;
import com.phonebook_system.report_service.client.ContactServiceClient;
import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.event.ReportRequestEvent;
import com.phonebook_system.report_service.model.exception.ReportNotFoundException;
import com.phonebook_system.report_service.model.response.*;
import com.phonebook_system.report_service.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    ContactServiceClient contactServiceClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final String topic = "report-request";

    @InjectMocks
    ReportService reportService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reportService, "topic", topic);
    }

    @Test
    void requestReport_Success() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportEntity report = ReportEntity.builder()
                .id(reportId)
                .requestDate(LocalDateTime.now())
                .status(ReportStatus.PREPARING)
                .build();
        when(reportRepository.save(any(ReportEntity.class))).thenReturn(report);

        // Act
        ReportResponse reportResponse = reportService.requestReport();

        // Assert
        assertNotNull(reportResponse);
        assertEquals(reportId, reportResponse.getId());

        verify(kafkaTemplate).send(eq(topic), eq(reportId.toString()), any(ReportRequestEvent.class));
        verify(reportRepository).save(any());

    }

    @Test
    void requestReport_DataBaseError_ThrowException() {
        // Arrange
        when(reportRepository.save(any(ReportEntity.class)))
                .thenThrow(new RuntimeException("Database Connection lost"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reportService.requestReport());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void requestReport_KafkaError_ThrowsException() {
        // Arrange
        ReportEntity savedReport = ReportEntity.builder().id(UUID.randomUUID()).build();
        when(reportRepository.save(any(ReportEntity.class))).thenReturn(savedReport);

        // Kafka hata fırlatıyor
        when(kafkaTemplate.send(any(), any(), any()))
                .thenThrow(new RuntimeException("Kafka broker not available"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reportService.requestReport());
    }

    @Test
    void listReports_Success() {
        // Arrange
        ReportEntity r1 = ReportEntity.builder().id(UUID.randomUUID()).status(ReportStatus.COMPLETED).build();
        ReportEntity r2 = ReportEntity.builder().id(UUID.randomUUID()).status(ReportStatus.PREPARING).build();
        List<ReportEntity> entities = List.of(r1, r2);

        when(reportRepository.findAll()).thenReturn(entities);

        // Act
        ReportListResponse result = reportService.listReports();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getReportList().size());
        verify(reportRepository).findAll();
    }

    @Test
    void getReportDetail_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ReportEntity entity = ReportEntity.builder().id(id).status(ReportStatus.COMPLETED).build();

        when(reportRepository.findWithDetailsById(id)).thenReturn(Optional.of(entity));

        // Act
        ReportDetailResponse result = reportService.getReportDetail(id);

        // Assert
        assertNotNull(result);
        assertEquals(ReportStatus.COMPLETED, result.getStatus());
        verify(reportRepository).findWithDetailsById(id);
    }

    @Test
    void getReportDetail_NotFound_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(reportRepository.findWithDetailsById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReportNotFoundException.class, () -> reportService.getReportDetail(id));
    }

    @Test
    void generateReport_Success() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportRequestEvent reportRequestEvent = ReportRequestEvent.builder()
                .reportId(reportId)
                .requestDate(LocalDateTime.now())
                .build();

        ReportEntity reportEntity = ReportEntity.builder()
                .id(reportId)
                .requestDate(LocalDateTime.now())
                .status(ReportStatus.PREPARING)
                .build();

        LocationStatisticListResponse locationStatisticListResponse = new LocationStatisticListResponse();
        LocationStatisticsResponse locationStatisticsResponse = new LocationStatisticsResponse();
        locationStatisticsResponse.setLocation("Adana");
        locationStatisticsResponse.setPersonCount(10L);
        locationStatisticsResponse.setPhoneNumberCount(20L);
        locationStatisticListResponse.setLocationList(List.of(locationStatisticsResponse));
        BaseResponseModel<LocationStatisticListResponse> feignResponse = BaseResponseModel
                .resultToResponse(locationStatisticListResponse);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(reportEntity));
        when(contactServiceClient.getLocationStats()).thenReturn(feignResponse);

        // Act
        reportService.generateReport(reportRequestEvent);

        // Assert
        assertEquals(ReportStatus.COMPLETED, reportEntity.getStatus());
        assertEquals(1, reportEntity.getDetails().size());
        assertEquals("Adana", reportEntity.getDetails().get(0).getLocation());
        verify(reportRepository).save(reportEntity);
    }

    @Test
    void generateReport_HandleException_SetsStatusFailed() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(reportId)
                .requestDate(LocalDateTime.now())
                .build();
        ReportEntity report = ReportEntity.builder()
                .id(reportId)
                .status(ReportStatus.PREPARING).build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        // dış service de hata alırsa status fail olacaktı.
        when(contactServiceClient.getLocationStats()).thenThrow(new RuntimeException("Service Down"));

        // Act
        reportService.generateReport(event);

        // Assert
        assertEquals(ReportStatus.FAILED, report.getStatus());
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    void generateReport_AlreadyCompleted_DoesNothing() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(reportId)
                .requestDate(LocalDateTime.now())
                .build();
        ReportEntity report = ReportEntity.builder()
                .id(reportId)
                .status(ReportStatus.COMPLETED).build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // Act
        reportService.generateReport(event);

        // Assert
        verify(contactServiceClient, never()).getLocationStats();
        verify(reportRepository, never()).save(any());
    }

    @Test
    void generateReport_ContactServiceReturnsSuccessFalse_SetsStatusFailed() {
        // Arrange
        UUID reportId = UUID.randomUUID();
        ReportEntity report = new ReportEntity();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        // Servis ayakta ama mantıksal hata dönüyor (isSuccess = false)
        BaseResponseModel<LocationStatisticListResponse> failResponse = BaseResponseModel.resultToResponse(null);

        when(contactServiceClient.getLocationStats()).thenReturn(failResponse);

        // Act
        reportService.generateReport(new ReportRequestEvent(reportId, LocalDateTime.now()));

        // Assert
        assertEquals(ReportStatus.FAILED, report.getStatus());
        verify(reportRepository).save(report);
    }

}