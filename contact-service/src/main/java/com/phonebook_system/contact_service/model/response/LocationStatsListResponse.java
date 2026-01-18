package com.phonebook_system.contact_service.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocationStatsListResponse {
    List<LocationStatsResponse> locationStats;
}
