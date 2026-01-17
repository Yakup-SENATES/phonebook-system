package com.phonebook_system.contact_service.controller;

import com.phonebook_system.contact_service.base.BaseResponseModel;
import com.phonebook_system.contact_service.model.request.PersonModel;
import com.phonebook_system.contact_service.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping
    public BaseResponseModel<PersonModel> create(@RequestBody PersonModel personRequest){
        return personService.createPerson(personRequest);
    }

    @DeleteMapping
    public BaseResponseModel<Void> delete(@RequestParam UUID uuid){
        return personService.deletePerson(uuid);
    }

}
