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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ContactService contactService;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    @Test
    void createPerson_Success() {
        // Arrange
        CreatePersonRequest request = new CreatePersonRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCompany("Setur");

        when(personRepository.save(any(PersonEntity.class))).thenAnswer(i -> {
            PersonEntity savedEntity = i.getArgument(0);
            savedEntity.setId(UUID.randomUUID());
            return savedEntity;
        });

        when(personMapper.toEntity(any(CreatePersonRequest.class))).thenReturn(new PersonEntity());
        when(personMapper.toResponse(any(PersonEntity.class))).thenAnswer(i -> {
            PersonEntity entity = i.getArgument(0);
            PersonResponse response = new PersonResponse();
            response.setId(entity.getId());
            return response;
        });

        // Act
        BaseResponseModel<PersonResponse> result = personService.createPerson(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData().getId());
        verify(personRepository).save(any(PersonEntity.class));
    }

    @Test
    void deletePerson_Success() {
        // Arrange
        UUID personId = UUID.randomUUID();
        PersonEntity entity = new PersonEntity();
        entity.setId(personId);

        when(personRepository.findById(personId)).thenReturn(Optional.of(entity));

        // Act
        BaseResponseModel<Void> result = personService.deletePerson(personId);

        // Assert
        assertNotNull(result);
        verify(personRepository).delete(entity);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void deletePerson_NotFound() {
        // Arrange
        UUID personId = UUID.randomUUID();
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.deletePerson(personId));
        verify(personRepository, never()).delete(any());
    }

    @Test
    void updatePerson_Success() {
        // Arrange
        UUID personId = UUID.randomUUID();
        // Eski Veri (DB'den gelen)
        PersonEntity existingEntity = new PersonEntity();
        existingEntity.setId(personId);
        existingEntity.setFirstName("Old Name");
        existingEntity.setLastName("Old Surname");
        existingEntity.setCompany("Old Company");

        // Guncelleme Talebi (Request)
        UpdatePersonRequest updateRequest = new UpdatePersonRequest();
        updateRequest.setFirstName("New Name");
        updateRequest.setLastName("New Surname");
        updateRequest.setCompany("New Company");

        when(personRepository.findById(personId)).thenReturn(Optional.of(existingEntity));
        // Save metodu, guncellenmis nesneyi geri dondurmeli
        when(personRepository.save(any(PersonEntity.class))).thenAnswer(i -> i.getArgument(0));

        doAnswer(invocation -> {
            PersonEntity entity = invocation.getArgument(1);
            UpdatePersonRequest req = invocation.getArgument(0);
            entity.setFirstName(req.getFirstName());
            entity.setLastName(req.getLastName());
            entity.setCompany(req.getCompany());
            return null;
        }).when(personMapper).updateEntityFromRequest(any(UpdatePersonRequest.class), any(PersonEntity.class));

        when(personMapper.toResponse(any(PersonEntity.class))).thenAnswer(i -> {
            PersonEntity entity = i.getArgument(0);
            PersonResponse response = new PersonResponse();
            response.setId(entity.getId());
            return response;
        });

        // Act
        BaseResponseModel<PersonResponse> result = personService.updatePerson(personId, updateRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData().getId());
        assertEquals("New Name", existingEntity.getFirstName());
        assertEquals("New Surname", existingEntity.getLastName());
        assertEquals("New Company", existingEntity.getCompany());
        verify(personRepository).findById(personId);
        verify(personRepository).save(any(PersonEntity.class));
    }

    @Test
    void updatePerson_NotFound() {
        // Arrange
        UUID personId = UUID.randomUUID();
        UpdatePersonRequest request = new UpdatePersonRequest();
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.updatePerson(personId, request));
        verify(personRepository, never()).save(any());
    }

    @Test
    void listPersons_Success() {
        // Arrange
        PersonEntity p1 = new PersonEntity();
        p1.setId(UUID.randomUUID());
        p1.setFirstName("Ahmet");

        PersonEntity p2 = new PersonEntity();
        p2.setId(UUID.randomUUID());
        p2.setFirstName("Mehmet");

        List<PersonEntity> entities = Arrays.asList(p1, p2);
        when(personRepository.findAll()).thenReturn(entities);

        when(personMapper.toResponseList(anyList())).thenAnswer(i -> {
            List<PersonEntity> list = i.getArgument(0);
            PersonResponse r1 = new PersonResponse();
            r1.setFirstName(list.get(0).getFirstName());
            PersonResponse r2 = new PersonResponse();
            r2.setFirstName(list.get(1).getFirstName());
            return Arrays.asList(r1, r2);
        });

        // Act
        BaseResponseModel<PersonListResponse> result = personService.listPersons();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().getPersons().size());
        assertEquals("Ahmet", result.getData().getPersons().get(0).getFirstName());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void listPersons_NotFound() {
        // Arrange: Repository bos liste donerse
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert: PersonsNotFoundException firlatmasini bekliyoruz
        assertThrows(PersonsNotFoundException.class, () -> personService.listPersons());
        verify(personRepository).findAll();
    }

    @Test
    void getPersonDetails_Success() {
        // Arrange
        UUID personId = UUID.randomUUID();
        PersonEntity entity = new PersonEntity();
        entity.setId(UUID.randomUUID());
        entity.setFirstName("Ahmet");

        when(personRepository.findWithContactsById(personId)).thenReturn(Optional.of(entity));

        when(personMapper.toDetailResponse(any(PersonEntity.class))).thenAnswer(i -> {
            PersonEntity e = i.getArgument(0);
            PersonDetailResponse response = new PersonDetailResponse();
            response.setFirstName(e.getFirstName());
            return response;
        });

        // Act
        BaseResponseModel<PersonDetailResponse> result = personService.getPersonDetails(personId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("Ahmet", result.getData().getFirstName());
    }

    @Test
    void getPersonDetails_NotFound() {
        // Arrange
        UUID personId = UUID.randomUUID();
        when(personRepository.findWithContactsById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.getPersonDetails(personId));
        verify(personRepository).findWithContactsById(personId);
    }

    @Test
    void addContactInfo_Success() {
        // Arrange
        UUID personId = UUID.randomUUID();
        // DB'den donecek olan kisiyi hazirla
        PersonEntity person = new PersonEntity();
        person.setId(personId);
        person.setFirstName("yakup");
        // Kaydedilmek istenen iletisim bilgisi istegi
        CreateContactInfoRequest request = new CreateContactInfoRequest();
        request.setType(ContactTypeEnum.PHONE);
        request.setValue("05551112233");

        ContactInfoResponse expectedResponse = new ContactInfoResponse();
        expectedResponse.setType(ContactTypeEnum.PHONE);
        expectedResponse.setValue("05551112233");

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(contactService.saveContactInfo(person, request)).thenReturn(expectedResponse);

        // Act
        BaseResponseModel<ContactInfoResponse> result = personService.addContactInfo(personId, request);

        // Assert
        assertNotNull(result);
        ContactInfoResponse data = result.getData();
        assertNotNull(data);
        assertEquals(expectedResponse, data);
        assertEquals(ContactTypeEnum.PHONE, data.getType());
        assertEquals("05551112233", data.getValue());

        verify(personRepository, times(1)).findById(personId);
        verify(contactService, times(1)).saveContactInfo(person, request);
    }

    @Test
    void addContactInfo_NotFound() {
        // Arrange
        UUID personId = UUID.randomUUID();
        CreateContactInfoRequest request = new CreateContactInfoRequest();
        request.setType(ContactTypeEnum.PHONE);
        request.setValue("05551112233");

        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> personService.addContactInfo(personId, request));

        verify(personRepository).findById(personId);
        verify(contactService, never()).saveContactInfo(any(), any());
    }

    @Test
    void removeContactInfo_Success() {
        // Arrange
        UUID personId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();
        PersonEntity person = new PersonEntity();
        person.setId(personId);
        ContactInfoEntity contactInfo = new ContactInfoEntity();
        contactInfo.setId(contactId);
        contactInfo.setPerson(person);

        when(contactService.getContactInfo(contactId)).thenReturn(contactInfo);

        // Act
        BaseResponseModel<Void> result = personService.removeContactInfo(personId, contactId);

        // Assert
        assertNotNull(result);
        verify(contactService, times(1)).deleteContactInfo(contactInfo);
    }

    @Test
    void removeContactInfo_InvalidOwner() {
        // Arrange
        UUID personId = UUID.randomUUID();
        UUID otherPersonId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        PersonEntity person = new PersonEntity();
        person.setId(personId);

        ContactInfoEntity contactInfo = new ContactInfoEntity();
        contactInfo.setId(contactId);
        contactInfo.setPerson(person);

        when(contactService.getContactInfo(contactId)).thenReturn(contactInfo);

        // Act & Assert
        InvalidContactInfoException exception = assertThrows(InvalidContactInfoException.class,
                () -> personService.removeContactInfo(otherPersonId, contactId));
        assertEquals("Contact info does not belong to this person", exception.getMessage());
        verify(contactService, never()).deleteContactInfo(any());
    }

    @Test
    void getLocationStats_Success() {
        // Arrange
        LocationStatsResponse stats = LocationStatsResponse.builder()
                .location("Istanbul")
                .personCount(10L)
                .phoneNumberCount(20L)
                .build();
        LocationStatsListResponse response = new LocationStatsListResponse();
        response.setLocationStats(List.of(stats));
        when(contactService.createLocationStat(ContactTypeEnum.LOCATION)).thenReturn(response);

        // Act
        BaseResponseModel<LocationStatsListResponse> result = personService.getLocationStats(ContactTypeEnum.LOCATION);

        // Assert
        assertNotNull(result);
        assertEquals(response, result.getData());
        assertEquals(1, result.getData().getLocationStats().size());
        assertEquals("Istanbul", result.getData().getLocationStats().get(0).getLocation());
        verify(contactService, times(1)).createLocationStat(ContactTypeEnum.LOCATION);
    }
}
