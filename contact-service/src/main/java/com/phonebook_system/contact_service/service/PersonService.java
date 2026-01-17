package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.converter.PersonConverter;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.model.request.PersonModel;
import com.phonebook_system.contact_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ContactService contactService;

    public BaseResponseModel<PersonModel> createPerson(PersonModel personRequest) {
        PersonEntity personEntity = PersonConverter.model2Entity(personRequest);
        PersonEntity save = personRepository.save(personEntity);
        PersonModel response = PersonConverter.entity2Model(save);
        return BaseResponseModel.resultToResponse(response);
    }

    public BaseResponseModel<Void> deletePerson(UUID uuid) {
        return BaseResponseModel.ok();
    }

    public void addContactInfo() {

    }

    public void removeContactInfo() {

    }

    public void listPersons() {

    }

    public void getPersonDetails(Long id) {

    }

}
