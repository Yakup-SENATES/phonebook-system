package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.mapper.PersonMapper;
import com.phonebook_system.contact_service.model.exception.InvalidContactInfoException;
import com.phonebook_system.contact_service.model.exception.PersonNotFoundException;
import com.phonebook_system.contact_service.model.exception.PersonsNotFoundException;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.request.CreatePersonRequest;
import com.phonebook_system.contact_service.model.request.UpdatePersonRequest;
import com.phonebook_system.contact_service.model.response.*;
import com.phonebook_system.contact_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ContactService contactService;
    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    @Transactional
    public BaseResponseModel<PersonResponse> createPerson(CreatePersonRequest request) {
        PersonEntity personEntity = personMapper.toEntity(request);
        PersonEntity savedPerson = personRepository.save(personEntity);
        PersonResponse response = personMapper.toResponse(savedPerson);
        return BaseResponseModel.resultToResponse(response);
    }

    @Transactional
    public BaseResponseModel<Void> deletePerson(UUID id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        personRepository.delete(person);
        return BaseResponseModel.ok();
    }

    @Transactional
    public BaseResponseModel<PersonResponse> updatePerson(UUID id, UpdatePersonRequest request) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        personMapper.updateEntityFromRequest(request, person);
        PersonEntity updatedPerson = personRepository.save(person);
        PersonResponse response = personMapper.toResponse(updatedPerson);
        return BaseResponseModel.resultToResponse(response);
    }

    // todo büyük datalar için pageable yapılabilir.
    @Transactional(readOnly = true)
    public BaseResponseModel<PersonListResponse> listPersons() {
        List<PersonEntity> persons = personRepository.findAll();
        if (CollectionUtils.isEmpty(persons)) {
            throw new PersonsNotFoundException();
        }
        List<PersonResponse> response = personMapper.toResponseList(persons);
        PersonListResponse wrapper = PersonListResponse.builder().persons(response).build();
        return BaseResponseModel.resultToResponse(wrapper);
    }

    @Transactional(readOnly = true)
    public BaseResponseModel<PersonDetailResponse> getPersonDetails(UUID id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        PersonDetailResponse response = personMapper.toDetailResponse(person);
        return BaseResponseModel.resultToResponse(response);
    }

    @Transactional
    public BaseResponseModel<ContactInfoResponse> addContactInfo(UUID personId, CreateContactInfoRequest request) {
        PersonEntity person = personRepository.findById(personId) // select
                .orElseThrow(() -> new PersonNotFoundException(personId));

        ContactInfoResponse response = contactService.saveContactInfo(person, request);
        return BaseResponseModel.resultToResponse(response);
    }

    @Transactional
    public BaseResponseModel<Void> removeContactInfo(UUID personId, UUID contactId) {
        ContactInfoEntity contactInfo = contactService.getContactInfo(contactId);

        if (!contactInfo.getPerson().getId().equals(personId)) {
            throw new InvalidContactInfoException("Contact info does not belong to this person");
        }

        contactService.deleteContactInfo(contactInfo);
        return BaseResponseModel.ok();
    }

    public BaseResponseModel<LocationStatsListResponse> getLocationStats() {
        LocationStatsListResponse locationStat = contactService.createLocationStat();
        return BaseResponseModel.resultToResponse(locationStat);
    }
}
