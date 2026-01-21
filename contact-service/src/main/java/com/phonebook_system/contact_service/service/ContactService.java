package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.mapper.ContactInfoMapper;
import com.phonebook_system.contact_service.model.ContactTypeEnum;
import com.phonebook_system.contact_service.model.exception.ContactInfoNotFoundException;
import com.phonebook_system.contact_service.model.exception.LocationStatsNotFoundException;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.response.ContactInfoResponse;
import com.phonebook_system.contact_service.model.response.LocationStatsResponse;
import com.phonebook_system.contact_service.model.response.LocationStatsListResponse;
import com.phonebook_system.contact_service.repository.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactInfoRepository contactInfoRepository;
    private final ContactInfoMapper contactInfoMapper; // = ContactInfoMapper.INSTANCE;

    /**
     * Saves contact info for a person.
     *
     * @param person  the person entity owner of this contact info
     * @param request the request object containing contact details
     * @return the saved contact info response
     */
    public ContactInfoResponse saveContactInfo(PersonEntity person, CreateContactInfoRequest request) {
        ContactInfoEntity contactInfo = contactInfoMapper.toEntity(request);
        contactInfo.setPerson(person);

        ContactInfoEntity savedContactInfo = contactInfoRepository.save(contactInfo);
        return contactInfoMapper.toResponse(savedContactInfo);
    }

    /**
     * Retrieves a contact info entity by ID.
     *
     * @param contactId the UUID of the contact info
     * @return the contact info entity
     * @throws ContactInfoNotFoundException if contact info is not found
     */
    public ContactInfoEntity getContactInfo(UUID contactId) {
        return contactInfoRepository.findById(contactId)
                .orElseThrow(() -> new ContactInfoNotFoundException(contactId));
    }

    /**
     * Deletes a contact info entity.
     *
     * @param contact the contact info entity to delete
     */
    public void deleteContactInfo(ContactInfoEntity contact) {
        contactInfoRepository.delete(contact);
    }

    /**
     * Creates and retrieves location statistics.
     * Generates a report of person and phone number counts per location.
     *
     * @return a list of location statistics wrapped in a response object
     * @throws LocationStatsNotFoundException if no location data is found
     */
    @Transactional(readOnly = true)
    public LocationStatsListResponse createLocationStat(ContactTypeEnum type) {
        List<ContactInfoEntity> contactInfos = contactInfoRepository.findAllByType(type);
        if (CollectionUtils.isEmpty(contactInfos)) {
            throw new LocationStatsNotFoundException();
        }

        List<String> values = contactInfos.stream()
                .map(ContactInfoEntity::getValue)
                .distinct()
                .toList();

        List<LocationStatsResponse> stats = values.stream().map(val -> LocationStatsResponse.builder()
                .location(val)
                .personCount(contactInfoRepository.countPersonsByLocation(val))
                .phoneNumberCount(contactInfoRepository.countPhoneNumbersByLocation(val))
                .build()).collect(Collectors.toList());
        LocationStatsListResponse wrapper = new LocationStatsListResponse();
        wrapper.setLocationStats(stats);
        return wrapper;
    }

}
