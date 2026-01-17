package com.phonebook_system.contact_service.model.response;

import com.phonebook_system.contact_service.model.ContactTypeEnum;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactInfoResponse {
    private UUID id;
    private ContactTypeEnum type;
    private String value;
}
