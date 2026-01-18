package com.phonebook_system.contact_service.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PersonListResponse {
    List<PersonResponse> persons;
}
