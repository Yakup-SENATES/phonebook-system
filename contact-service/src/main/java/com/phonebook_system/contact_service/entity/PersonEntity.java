package com.phonebook_system.contact_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Entity
@Table(name = "t_person")
@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonEntity {
    @Id
    private UUID id = UUID.randomUUID();
    private String firstName;
    private String lastName;
    private String company;
}
