package com.phonebook_system.report_service.model.response;

import com.phonebook_system.report_service.model.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDetailResponse {
    private UUID id;
    private LocalDateTime requestDate;
    private ReportStatus status;

    @Builder.Default
    private List<LocationStatisticsResponse> details = new ArrayList<>();
}
