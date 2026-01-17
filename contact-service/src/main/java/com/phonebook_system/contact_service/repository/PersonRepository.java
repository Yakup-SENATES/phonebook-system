package com.phonebook_system.contact_service.repository;

import com.phonebook_system.contact_service.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity,Long> {
}
