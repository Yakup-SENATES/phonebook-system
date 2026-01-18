package com.phonebook_system.contact_service.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationStatsResponse {
    private String location;
    private Long personCount;
    private Long phoneNumberCount;
}
