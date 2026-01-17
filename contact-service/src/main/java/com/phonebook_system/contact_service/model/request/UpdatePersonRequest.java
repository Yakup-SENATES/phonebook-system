package com.phonebook_system.contact_service.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePersonRequest {
    private String firstName;
    private String lastName;
    private String company;
}
