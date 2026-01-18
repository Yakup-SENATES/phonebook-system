package com.phonebook_system.report_service.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationStatisticsResponse {
    private String location;
    private Long personCount;
    private Long phoneNumberCount;
}
