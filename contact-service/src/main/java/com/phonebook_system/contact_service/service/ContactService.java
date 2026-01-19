package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.mapper.ContactInfoMapper;
import com.phonebook_system.contact_service.model.exception.ContactInfoNotFoundException;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.response.ContactInfoResponse;
import com.phonebook_system.contact_service.model.response.LocationStatsResponse;
import com.phonebook_system.contact_service.model.response.LocationStatsListResponse;
import com.phonebook_system.contact_service.repository.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactInfoRepository contactInfoRepository;
    private final ContactInfoMapper contactInfoMapper = ContactInfoMapper.INSTANCE;

    public ContactInfoResponse saveContactInfo(PersonEntity person, CreateContactInfoRequest request) {
        ContactInfoEntity contactInfo = contactInfoMapper.toEntity(request);
        contactInfo.setPerson(person);

        ContactInfoEntity savedContactInfo = contactInfoRepository.save(contactInfo);
        return contactInfoMapper.toResponse(savedContactInfo);
    }

    public ContactInfoEntity getContactInfo(UUID contactId) {
        return contactInfoRepository.findById(contactId)
                .orElseThrow(() -> new ContactInfoNotFoundException(contactId));
    }

    public void deleteContactInfo(ContactInfoEntity contact) {
        contactInfoRepository.delete(contact);
    }

    @Transactional(readOnly = true)
    public LocationStatsListResponse createLocationStat() {
        List<String> locations = contactInfoRepository.findAllUniqueLocations();
        List<LocationStatsResponse> stats = locations.stream().map(loc -> LocationStatsResponse.builder()
                .location(loc)
                .personCount(contactInfoRepository.countPersonsByLocation(loc))
                .phoneNumberCount(contactInfoRepository.countPhoneNumbersByLocation(loc))
                .build()).collect(Collectors.toList());
        LocationStatsListResponse wrapper = new LocationStatsListResponse();
        wrapper.setLocationStats(stats);
        return wrapper;
    }

}
