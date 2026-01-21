package com.phonebook_system.contact_service.service;

import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import com.phonebook_system.contact_service.entity.PersonEntity;
import com.phonebook_system.contact_service.mapper.ContactInfoMapper;
import com.phonebook_system.contact_service.model.ContactTypeEnum;
import com.phonebook_system.contact_service.model.exception.ContactInfoNotFoundException;
import com.phonebook_system.contact_service.model.exception.LocationStatsNotFoundException;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.response.ContactInfoResponse;
import com.phonebook_system.contact_service.model.response.LocationStatsListResponse;
import com.phonebook_system.contact_service.model.response.LocationStatsResponse;
import com.phonebook_system.contact_service.repository.ContactInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactInfoRepository contactInfoRepository;

    @Mock
    private ContactInfoMapper contactInfoMapper;

    @InjectMocks
    private ContactService contactService;

    @Test
    void saveContactInfo_Success() {
        // Arrange
        PersonEntity person = new PersonEntity();
        UUID id = UUID.randomUUID();
        person.setId(id);

        CreateContactInfoRequest request = CreateContactInfoRequest.builder()
                .type(ContactTypeEnum.LOCATION)
                .value("Istanbul")
                .build();

        when(contactInfoMapper.toEntity(request)).thenReturn(new ContactInfoEntity());
        when(contactInfoMapper.toResponse(any(ContactInfoEntity.class))).thenAnswer(invocation -> {
            ContactInfoEntity entity = invocation.getArgument(0);
            ContactInfoResponse response = new ContactInfoResponse();
            response.setId(entity.getId());
            response.setType(entity.getType());
            response.setValue(entity.getValue());
            return response;
        });

        when(contactInfoRepository.save(any(ContactInfoEntity.class)))
                .thenAnswer(invocation -> {
                    ContactInfoEntity savedEntity = invocation.getArgument(0);
                    savedEntity.setPerson(person);
                    savedEntity.setId(id);
                    savedEntity.setType(request.getType());
                    savedEntity.setValue(request.getValue());
                    return savedEntity;
                });

        // Act
        ContactInfoResponse result = contactService.saveContactInfo(person, request);

        // Assert
        assertNotNull(result);
        assertEquals(ContactTypeEnum.LOCATION, result.getType());
        assertEquals("Istanbul", result.getValue());
        assertEquals(id, result.getId());
        verify(contactInfoRepository).save(any(ContactInfoEntity.class));
    }

    @Test
    void getContactInfo_Success() {
        // Arrange
        UUID contactId = UUID.randomUUID();
        ContactInfoEntity expectedEntity = ContactInfoEntity.builder()
                .id(contactId)
                .type(ContactTypeEnum.PHONE)
                .value("5551234567")
                .build();

        when(contactInfoRepository.findById(contactId)).thenReturn(Optional.of(expectedEntity));

        // Act
        ContactInfoEntity result = contactService.getContactInfo(contactId);

        // Assert
        assertNotNull(result, "Donen sonuc null olmamali");
        assertEquals(contactId, result.getId(), "Donen ID talep edilenle ayni olmali");
        assertEquals(ContactTypeEnum.PHONE, result.getType(), "Veri tipi bozulmadan donmeli");
        assertEquals("5551234567", result.getValue(), "Veri icerigi bozulmadan donmeli");
        assertEquals(expectedEntity, result);
    }

    @Test
    void getContactInfo_NotFound() {
        // Arrange
        UUID contactId = UUID.randomUUID();
        when(contactInfoRepository.findById(contactId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContactInfoNotFoundException.class, () -> contactService.getContactInfo(contactId));
    }

    @Test
    void deleteContactInfo_Success() {
        // Arrange
        UUID contactId = UUID.randomUUID();
        ContactInfoEntity entityToDelete = ContactInfoEntity.builder()
                .id(contactId)
                .type(ContactTypeEnum.EMAIL)
                .value("yakuppsenates@gmail.com")
                .build();

        // Act
        contactService.deleteContactInfo(entityToDelete);

        // Assert
        // Repository'nin delete metodu cagrildi mi?
        verify(contactInfoRepository, times(1)).delete(entityToDelete);
        // Silinmeye calisilan nesnenin icerigi dogru mu?
        assertNotNull(entityToDelete.getId());
        assertEquals(ContactTypeEnum.EMAIL, entityToDelete.getType());
        verifyNoMoreInteractions(contactInfoRepository);
    }

    @Test
    void createLocationStat_Success() {
        // Arrange
        String location1 = "Istanbul";
        String location2 = "Ankara";
        List<String> locations = Arrays.asList(location1, location2);

        ContactInfoEntity info1 = ContactInfoEntity.builder().value(location1).type(ContactTypeEnum.LOCATION).build();
        ContactInfoEntity info2 = ContactInfoEntity.builder().value(location2).type(ContactTypeEnum.LOCATION).build();
        List<ContactInfoEntity> contactInfos = Arrays.asList(info1, info2);

        when(contactInfoRepository.findAllByType(ContactTypeEnum.LOCATION)).thenReturn(contactInfos);
        // istanbul verileri
        when(contactInfoRepository.countPersonsByLocation(location1)).thenReturn(10L);
        when(contactInfoRepository.countPhoneNumbersByLocation(location1)).thenReturn(20L);
        // ankara verileri
        when(contactInfoRepository.countPersonsByLocation(location2)).thenReturn(5L);
        when(contactInfoRepository.countPhoneNumbersByLocation(location2)).thenReturn(10L);

        // Act
        LocationStatsListResponse result = contactService.createLocationStat(ContactTypeEnum.LOCATION);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLocationStats());
        assertEquals(2, result.getLocationStats().size());

        LocationStatsResponse istanbulStat = result.getLocationStats().stream()
                .filter(s -> s.getLocation().equals(location1)).findFirst().orElseThrow();
        assertEquals(10L, istanbulStat.getPersonCount());
        assertEquals(20L, istanbulStat.getPhoneNumberCount());

        LocationStatsResponse ankaraStat = result.getLocationStats().stream()
                .filter(s -> s.getLocation().equals(location2)).findFirst().orElseThrow();
        assertEquals(5L, ankaraStat.getPersonCount());
        assertEquals(10L, ankaraStat.getPhoneNumberCount());

        // behavior
        verify(contactInfoRepository, times(1)).findAllByType(ContactTypeEnum.LOCATION);
        verify(contactInfoRepository, times(1)).countPersonsByLocation(location1);
        verify(contactInfoRepository, times(1)).countPhoneNumbersByLocation(location1);
        verify(contactInfoRepository, times(1)).countPersonsByLocation(location2);
        verify(contactInfoRepository, times(1)).countPhoneNumbersByLocation(location2);
    }

    @Test
    void createLocationStat_ThrowsException_WhenLocationsEmpty() {
        // Arrange
        when(contactInfoRepository.findAllByType(ContactTypeEnum.LOCATION)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(LocationStatsNotFoundException.class,
                () -> contactService.createLocationStat(ContactTypeEnum.LOCATION));

        // Alt metodlarin hic cagrilmadigini dogrula
        verify(contactInfoRepository, never()).countPersonsByLocation(anyString());
        verify(contactInfoRepository, never()).countPhoneNumbersByLocation(anyString());
    }

}
