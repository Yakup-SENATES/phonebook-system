package com.phonebook_system.contact_service.model.request;

;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonModel {
    private String firstName;
    private String lastName;
    private String company;
}
