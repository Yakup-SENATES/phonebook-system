package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.model.event.ReportRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportRequestProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    public UUID sendReportRequest() {
        UUID reportId = UUID.randomUUID();
        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(reportId)
                .requestDate(LocalDateTime.now())
                .build();
        kafkaTemplate.send(topic, reportId.toString(), event);
        return reportId;
    }
}
