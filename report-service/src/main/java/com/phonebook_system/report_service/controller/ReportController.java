package com.phonebook_system.report_service.controller;

import com.phonebook_system.report_service.base.BaseResponseModel;
import com.phonebook_system.report_service.model.response.ReportDetailResponse;
import com.phonebook_system.report_service.model.response.ReportListResponse;
import com.phonebook_system.report_service.model.response.ReportResponse;
import com.phonebook_system.report_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/request")
    public ResponseEntity<BaseResponseModel<ReportResponse>> requestReport() {
        return ResponseEntity.ok(reportService.requestReport());
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponseModel<ReportListResponse>> listReports() {
        return ResponseEntity.ok(reportService.listReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseModel<ReportDetailResponse>> getReportDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportDetail(id));
    }
}
