package com.phonebook_system.contact_service.model.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDetailResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String company;

    @Builder.Default
    private List<ContactInfoResponse> contactInfoList = new ArrayList<>();
}
