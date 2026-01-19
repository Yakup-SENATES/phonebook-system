package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.model.event.ReportRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportRequestProducerTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    ReportRequestProducer reportRequestProducer;

    private final String topic = "report-requests";

    @BeforeEach
    void setUp() {
        // value ile gelen alanin manuel setlenmesi
        ReflectionTestUtils.setField(reportRequestProducer, "topic", topic);
    }

    @Test
    void sendReportRequest_Success() {
        // Act
        UUID resultId = reportRequestProducer.sendReportRequest();

        // Assert
        assertNotNull(resultId);

        // Kafka'ya gonderilen mesaji dogrula
        // verify(mock).send(topic, key, data)
        verify(kafkaTemplate, times(1)).send(
                eq(topic),
                eq(resultId.toString()),
                argThat(argument -> {
                    ReportRequestEvent event = (ReportRequestEvent) argument;
                    return event.getReportId().equals(resultId) &&
                            event.getRequestDate() != null;
                })
        );
    }


}