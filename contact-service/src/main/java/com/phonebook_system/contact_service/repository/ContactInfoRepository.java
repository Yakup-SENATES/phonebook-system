package com.phonebook_system.contact_service.repository;

import com.phonebook_system.contact_service.entity.ContactInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactInfoRepository extends JpaRepository<ContactInfoEntity, UUID> {

    @Query("SELECT DISTINCT c.value FROM ContactInfoEntity c WHERE c.type = 'LOCATION'")
    List<String> findAllUniqueLocations();

    @Query("SELECT COUNT(DISTINCT c.person.id) FROM ContactInfoEntity c WHERE c.type = 'LOCATION' AND c.value = :location")
    Long countPersonsByLocation(String location);

    @Query("SELECT COUNT(c) FROM ContactInfoEntity c WHERE c.type = 'PHONE' AND c.person.id IN " +
            "(SELECT DISTINCT c2.person.id FROM ContactInfoEntity c2 WHERE c2.type = 'LOCATION' AND c2.value = :location)")
    Long countPhoneNumbersByLocation(String location);
}
