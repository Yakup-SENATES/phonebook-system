package com.phonebook_system.contact_service.controller;

import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.model.ContactTypeEnum;
import com.phonebook_system.contact_service.model.request.CreateContactInfoRequest;
import com.phonebook_system.contact_service.model.request.CreatePersonRequest;
import com.phonebook_system.contact_service.model.request.UpdatePersonRequest;
import com.phonebook_system.contact_service.model.response.*;
import com.phonebook_system.contact_service.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<BaseResponseModel<PersonResponse>> createPerson(
            @Valid @RequestBody CreatePersonRequest request) {
        BaseResponseModel<PersonResponse> response = personService.createPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseModel<PersonResponse>> updatePerson(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePersonRequest request) {
        BaseResponseModel<PersonResponse> response = personService.updatePerson(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseModel<Void>> deletePerson(@PathVariable UUID id) {
        BaseResponseModel<Void> response = personService.deletePerson(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponseModel<PersonListResponse>> listPersons() {
        BaseResponseModel<PersonListResponse> response = personService.listPersons();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseModel<PersonDetailResponse>> getPersonDetails(@PathVariable UUID id) {
        BaseResponseModel<PersonDetailResponse> response = personService.getPersonDetails(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/location-stats")
    public ResponseEntity<BaseResponseModel<LocationStatsListResponse>> getLocationStats(
            @RequestParam(defaultValue = "LOCATION") ContactTypeEnum type) {
        BaseResponseModel<LocationStatsListResponse> response = personService.getLocationStats(type);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{personId}/contacts")
    public ResponseEntity<BaseResponseModel<ContactInfoResponse>> addContactInfo(
            @PathVariable UUID personId,
            @Valid @RequestBody CreateContactInfoRequest request) {
        BaseResponseModel<ContactInfoResponse> response = personService.addContactInfo(personId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{personId}/contacts/{contactId}")
    public ResponseEntity<BaseResponseModel<Void>> removeContactInfo(
            @PathVariable UUID personId,
            @PathVariable UUID contactId) {
        BaseResponseModel<Void> response = personService.removeContactInfo(personId, contactId);
        return ResponseEntity.ok(response);
    }

}
