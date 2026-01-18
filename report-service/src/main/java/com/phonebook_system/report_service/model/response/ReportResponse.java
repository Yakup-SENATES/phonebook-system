package com.phonebook_system.report_service.model.response;

import com.phonebook_system.report_service.model.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    private UUID id;
    private LocalDateTime requestDate;
    private ReportStatus status;
}
