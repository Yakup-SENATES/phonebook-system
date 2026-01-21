package com.phonebook_system.report_service.mapper;

import com.phonebook_system.report_service.entity.ReportDetailEntity;
import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.response.LocationStatisticsResponse;
import com.phonebook_system.report_service.model.response.ReportDetailResponse;
import com.phonebook_system.report_service.model.response.ReportResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportResponse toResponse(ReportEntity entity);

    ReportDetailResponse toDetailResponse(ReportEntity entity);

    List<ReportResponse> toResponseList(List<ReportEntity> entities);

    LocationStatisticsResponse toDetailResponse(ReportDetailEntity entity);
}
