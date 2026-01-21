package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.mapper.PersonMapper;
import com.phonebook_system.contact_service.model.ContactTypeEnum;
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

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ContactService contactService;
    private final PersonMapper personMapper;

    /**
     * Creates a new person record in the system.
     *
     * @param request the request object containing person details
     * @return the created person wrapped in a response model
     */
    @Transactional
    public BaseResponseModel<PersonResponse> createPerson(CreatePersonRequest request) {
        try {
            PersonEntity personEntity = personMapper.toEntity(request);
            PersonEntity savedPerson = personRepository.save(personEntity);
            PersonResponse response = personMapper.toResponse(savedPerson);
            return BaseResponseModel.resultToResponse(response);
        } catch (DataIntegrityViolationException e) {
            throw new PersonsNotFoundException(); // Using existing exception or generic RuntimeException for now,
                                                  // ideally should be distinct
        }
    }

    /**
     * Deletes a person by their unique identifier.
     *
     * @param id the UUID of the person to delete
     * @return a successful response model
     * @throws PersonNotFoundException if the person is not found
     */
    @Transactional
    public BaseResponseModel<Void> deletePerson(UUID id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        personRepository.delete(person);
        return BaseResponseModel.ok();
    }

    /**
     * Updates an existing person's details.
     *
     * @param id      the UUID of the person to update
     * @param request the request object containing new details
     * @return the updated person wrapped in a response model
     * @throws PersonNotFoundException if the person is not found
     */
    @Transactional
    public BaseResponseModel<PersonResponse> updatePerson(UUID id, UpdatePersonRequest request) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        personMapper.updateEntityFromRequest(request, person);
        PersonEntity updatedPerson = personRepository.save(person);
        PersonResponse response = personMapper.toResponse(updatedPerson);
        return BaseResponseModel.resultToResponse(response);
    }

    /**
     * Lists all persons currently in the system.
     *
     * @return a list of all persons wrapped in a response model
     * @throws PersonsNotFoundException if no persons are found
     */
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

    /**
     * Retrieves detailed information about a person, including their contacts.
     *
     * @param id the UUID of the person
     * @return the person details wrapped in a response model
     * @throws PersonNotFoundException if the person is not found
     */
    @Transactional(readOnly = true)
    public BaseResponseModel<PersonDetailResponse> getPersonDetails(UUID id) {
        PersonEntity person = personRepository.findWithContactsById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        PersonDetailResponse response = personMapper.toDetailResponse(person);
        return BaseResponseModel.resultToResponse(response);
    }

    /**
     * Adds contact information to a specific person.
     *
     * @param personId the UUID of the person
     * @param request  the request object containing contact info details
     * @return the added contact info wrapped in a response model
     * @throws PersonNotFoundException if the person is not found
     */
    @Transactional
    public BaseResponseModel<ContactInfoResponse> addContactInfo(UUID personId, CreateContactInfoRequest request) {
        PersonEntity person = personRepository.findById(personId) // select
                .orElseThrow(() -> new PersonNotFoundException(personId));

        ContactInfoResponse response = contactService.saveContactInfo(person, request);
        return BaseResponseModel.resultToResponse(response);
    }

    /**
     * Removes a specific contact information from a person.
     *
     * @param personId  the UUID of the person
     * @param contactId the UUID of the contact info to remove
     * @return a successful response model
     * @throws InvalidContactInfoException if the contact info does not belong to
     *                                     the person
     */
    @Transactional
    public BaseResponseModel<Void> removeContactInfo(UUID personId, UUID contactId) {
        ContactInfoEntity contactInfo = contactService.getContactInfo(contactId);

        if (!contactInfo.getPerson().getId().equals(personId)) {
            throw new InvalidContactInfoException("Contact info does not belong to this person");
        }

        contactService.deleteContactInfo(contactInfo);
        return BaseResponseModel.ok();
    }

    /**
     * Retrieves contact statistics based on contactType information.
     *
     * @return a list of location statistics wrapped in a response model
     */
    public BaseResponseModel<LocationStatsListResponse> getLocationStats(ContactTypeEnum type) {
        LocationStatsListResponse locationStat = contactService.createLocationStat(type);
        return BaseResponseModel.resultToResponse(locationStat);
    }
}
