package com.phonebook_system.contact_service.model.request;

import com.phonebook_system.contact_service.model.ContactTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ContactInfoModel {
    private UUID id;
    private ContactTypeEnum type;
    private String value;
}
