package com.phonebook_system.report_service.model.event;

import com.phonebook_system.report_service.model.ContactTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequestEvent {
    private UUID reportId;
    private LocalDateTime requestDate;
    private ContactTypeEnum contactType;
}
