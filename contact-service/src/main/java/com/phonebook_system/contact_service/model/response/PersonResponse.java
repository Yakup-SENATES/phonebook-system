package com.phonebook_system.contact_service.model.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String company;
}
