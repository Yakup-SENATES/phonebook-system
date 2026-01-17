package com.phonebook_system.contact_service.entity;

import com.phonebook_system.contact_service.model.ContactTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_contact_info")
@Getter
@Setter
/*
    * Person (1) → ContactInfo (N)
 */
public class ContactInfoEntity {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "person_uuid")
    private PersonEntity person;

    private ContactTypeEnum type;

    private String value;
}
