package com.phonebook_system.report_service.repository;

import com.phonebook_system.report_service.entity.ReportEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {


    @Query("SELECT r FROM ReportEntity r WHERE r.status <> 'COMPLETED'")
    Optional<ReportEntity> findByStatusNotCompleted();

    @EntityGraph(attributePaths = "details")
    Optional<ReportEntity> findWithDetailsById(UUID id);
}
