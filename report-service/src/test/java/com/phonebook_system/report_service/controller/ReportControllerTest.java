package com.phonebook_system.report_service.controller;

import com.phonebook_system.report_service.model.ContactTypeEnum;
import com.phonebook_system.report_service.model.ReportStatus;
import com.phonebook_system.report_service.model.exception.ReportNotFoundException;
import com.phonebook_system.report_service.model.response.ReportDetailResponse;
import com.phonebook_system.report_service.model.response.ReportListResponse;
import com.phonebook_system.report_service.model.response.ReportResponse;
import com.phonebook_system.report_service.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        ReportService reportService;

        @Test
        void requestReport_ShouldReturnOk() throws Exception {
                // Arrange
                ReportResponse response = ReportResponse.builder()
                                .id(UUID.randomUUID())
                                .status(ReportStatus.PREPARING)
                                .build();

                when(reportService.requestReport(ContactTypeEnum.LOCATION)).thenReturn(response);

                // Act & Assert
                mockMvc.perform(post("/api/reports/request")
                                .param("type", "LOCATION")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("PREPARING"));
        }

        @Test
        void listReports_ShouldReturnOk() throws Exception {
                // Arrange
                ReportListResponse response = ReportListResponse.builder()
                                .reportList(Collections.emptyList())
                                .build();

                when(reportService.listReports()).thenReturn(response);

                // Act & Assert
                mockMvc.perform(get("/api/reports/list"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.reportList").isArray());
        }

        @Test
        void getReportDetail_ShouldReturnOk() throws Exception {
                // Arrange
                UUID id = UUID.randomUUID();
                ReportDetailResponse response = new ReportDetailResponse();

                when(reportService.getReportDetail(id)).thenReturn(response);

                // Act & Assert
                mockMvc.perform(get("/api/reports/{id}", id))
                                .andExpect(status().isOk());
        }

        @Test
        void getReportDetail_NotFound_ShouldReturn404() throws Exception {
                // Arrange
                UUID id = UUID.randomUUID();
                // Eğer bir GlobalExceptionHandler sınıfın varsa bu 404 döner
                when(reportService.getReportDetail(id)).thenThrow(new ReportNotFoundException(id));

                // Act & Assert
                mockMvc.perform(get("/api/reports/{id}", id))
                                .andExpect(status().isNotFound());
        }

}