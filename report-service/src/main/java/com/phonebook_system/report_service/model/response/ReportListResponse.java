package com.phonebook_system.report_service.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class ReportListResponse {
    private List<ReportResponse> reportList;
}
