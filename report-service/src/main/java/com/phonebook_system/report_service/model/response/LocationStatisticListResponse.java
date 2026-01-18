package com.phonebook_system.report_service.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocationStatisticListResponse {
    private List<LocationStatisticsResponse> locationList;
}
