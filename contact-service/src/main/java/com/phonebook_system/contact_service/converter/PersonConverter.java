package com.phonebook_system.contact_service.converter;

import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.model.request.PersonModel;

import java.util.UUID;

public class PersonConverter {


    public static PersonEntity model2Entity(PersonModel personRequest) {
     return PersonEntity.builder()
             .id(UUID.randomUUID())
             .firstName(personRequest.getFirstName())
             .lastName(personRequest.getLastName())
             .company(personRequest.getCompany())
             .build();

    }

    public static PersonModel entity2Model(PersonEntity personEntity) {
     return  PersonModel.builder()
             .firstName(personEntity.getFirstName())
             .lastName(personEntity.getLastName())
             .company(personEntity.getCompany()).build();
    }


}
