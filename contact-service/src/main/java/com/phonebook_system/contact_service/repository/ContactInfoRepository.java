package com.phonebook_system.contact_service.repository;

import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactInfoRepository extends JpaRepository<ContactInfoEntity,Long> {
}
