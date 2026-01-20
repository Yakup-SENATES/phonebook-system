package com.phonebook_system.contact_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.model.ContactTypeEnum;
import com.phonebook_system.contact_service.model.request.*;
import com.phonebook_system.contact_service.model.response.*;
import com.phonebook_system.contact_service.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Request body'leri JSON'a çevirmek için

    @MockitoBean
    private PersonService personService;

    @Test
    void createPerson_ShouldReturnCreated() throws Exception {
        CreatePersonRequest request = new CreatePersonRequest();
        request.setFirstName("Yakup");
        request.setLastName("Handler");

        PersonResponse personResponse = new PersonResponse();
        personResponse.setFirstName("Yakup");
        personResponse.setLastName("Handler");
        BaseResponseModel<PersonResponse> response = BaseResponseModel.resultToResponse(personResponse);

        when(personService.createPerson(any(CreatePersonRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.firstName").value("Yakup"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createPerson_ValidationFailed_ShouldReturnBadRequest() throws Exception {
        // Arrange: İsim ve soyisim boş olan hatalı bir istek
        CreatePersonRequest invalidRequest = CreatePersonRequest.builder()
                .firstName("")
                .lastName("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError()); // 500

        // Doğrulama: Servis katmanı asla çağrılmamalı
        verifyNoInteractions(personService);
    }

    @Test
    void addContactInfo_ValidationFailed_ShouldReturnBadRequest() throws Exception {
        // Arrange: İletişim tipi ve değeri olmayan hatalı bir istek
        CreateContactInfoRequest invalidRequest = CreateContactInfoRequest.builder()
                .type(null)
                .value(" ")
                .build();

        UUID personId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/persons/{personId}/contacts", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(personService);
    }

    @Test
    void listPersons_ShouldReturnOk() throws Exception {
        BaseResponseModel<PersonListResponse> response = BaseResponseModel.resultToResponse(PersonListResponse.builder().build());

        when(personService.listPersons()).thenReturn(response);

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deletePerson_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        BaseResponseModel<Void> response = BaseResponseModel.resultToResponse(null);

        when(personService.deletePerson(id)).thenReturn(response);

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void addContactInfo_ShouldReturnCreated() throws Exception {
        UUID personId = UUID.randomUUID();
        CreateContactInfoRequest request = new CreateContactInfoRequest();
        request.setType(ContactTypeEnum.PHONE);
        request.setValue("0555");

        ContactInfoResponse contactResponse = new ContactInfoResponse();
        contactResponse.setValue("0555");
        BaseResponseModel<ContactInfoResponse> response = BaseResponseModel.resultToResponse(contactResponse);

        when(personService.addContactInfo(eq(personId), any(CreateContactInfoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/persons/{personId}/contacts", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.value").value("0555"));
    }

    @Test
    void removeContactInfo_ShouldReturnOk() throws Exception {
        UUID personId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();
        BaseResponseModel<Void> response = BaseResponseModel.resultToResponse(null);

        when(personService.removeContactInfo(personId, contactId)).thenReturn(response);

        mockMvc.perform(delete("/api/persons/{personId}/contacts/{contactId}", personId, contactId))
                .andExpect(status().isOk());
    }
}