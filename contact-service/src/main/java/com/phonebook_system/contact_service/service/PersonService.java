package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.mapper.ContactInfoMapper;
import com.phonebook_system.contact_service.mapper.PersonMapper;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.request.CreatePersonRequest;
import com.phonebook_system.contact_service.model.request.UpdatePersonRequest;
import com.phonebook_system.contact_service.model.event.ReportRequestEvent;
import com.phonebook_system.contact_service.model.response.*;
import com.phonebook_system.contact_service.repository.ContactInfoRepository;
import com.phonebook_system.contact_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final ContactInfoRepository contactInfoRepository;
    private final PersonMapper personMapper;
    private final ContactInfoMapper contactInfoMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        personRepository.delete(person);
        return BaseResponseModel.ok();
    }

    @Transactional
    public BaseResponseModel<PersonResponse> updatePerson(UUID id, UpdatePersonRequest request) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        personMapper.updateEntityFromRequest(request, person);
        PersonEntity updatedPerson = personRepository.save(person);
        PersonResponse response = personMapper.toResponse(updatedPerson);
        return BaseResponseModel.resultToResponse(response);
    }
    // todo büyük datalar için pageable yapılabilir.
    @Transactional(readOnly = true)
    public BaseResponseModel<List<PersonResponse>> listPersons() {
        List<PersonEntity> persons = personRepository.findAll();
        List<PersonResponse> response = personMapper.toResponseList(persons);
        return BaseResponseModel.resultToResponse(response);
    }

    @Transactional(readOnly = true)
    public BaseResponseModel<PersonDetailResponse> getPersonDetails(UUID id) {
        PersonEntity person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        PersonDetailResponse response = personMapper.toDetailResponse(person);
        return BaseResponseModel.resultToResponse(response);
    }

    @Transactional
    public BaseResponseModel<ContactInfoResponse> addContactInfo(UUID personId, CreateContactInfoRequest request) {
        PersonEntity person = personRepository.findById(personId) // select
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + personId));

        ContactInfoEntity contactInfo = contactInfoMapper.toEntity(request);
        contactInfo.setPerson(person);
        ContactInfoEntity savedContactInfo = contactInfoRepository.save(contactInfo); // insert

        ContactInfoResponse response = contactInfoMapper.toResponse(savedContactInfo);
        return BaseResponseModel.resultToResponse(response);
    }

    @Transactional
    public BaseResponseModel<Void> removeContactInfo(UUID personId, UUID contactId) {
        PersonEntity person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + personId));

        ContactInfoEntity contactInfo = contactInfoRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact info not found with id: " + contactId));

        if (!contactInfo.getPerson().getId().equals(personId)) {
            throw new RuntimeException("Contact info does not belong to this person");
        }

        contactInfoRepository.delete(contactInfo);
        return BaseResponseModel.ok();
    }

    @Transactional(readOnly = true)
    public BaseResponseModel<List<LocationStatsResponse>> getLocationStats() {
        List<String> locations = contactInfoRepository.findAllUniqueLocations();
        List<LocationStatsResponse> stats = locations.stream().map(loc -> LocationStatsResponse.builder()
                .location(loc)
                .personCount(contactInfoRepository.countPersonsByLocation(loc))
                .phoneNumberCount(contactInfoRepository.countPhoneNumbersByLocation(loc))
                .build()).collect(Collectors.toList());
        return BaseResponseModel.resultToResponse(stats);
    }

    public void requestReport() {
        ReportRequestEvent event = ReportRequestEvent.builder()
                .reportId(UUID.randomUUID())
                .requestDate(LocalDateTime.now())
                .build();
        kafkaTemplate.send("report-requests", event);
    }
}
