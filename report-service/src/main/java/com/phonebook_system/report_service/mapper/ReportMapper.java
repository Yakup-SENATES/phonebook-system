package com.phonebook_system.report_service.mapper;

import com.phonebook_system.report_service.entity.ReportDetailEntity;
import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.response.LocationStatisticsResponse;
import com.phonebook_system.report_service.model.response.ReportDetailResponse;
import com.phonebook_system.report_service.model.response.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    ReportResponse toResponse(ReportEntity entity);

    ReportDetailResponse toDetailResponse(ReportEntity entity);

    List<ReportResponse> toResponseList(List<ReportEntity> entities);

    LocationStatisticsResponse toDetailResponse(ReportDetailEntity entity);
}
