package com.phonebook_system.contact_service.repository;

import com.phonebook_system.contact_service.entity.PersonEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    @EntityGraph(attributePaths = {"contactInfoList"})
    Optional<PersonEntity> findByIdWithId(UUID uuid);
}
