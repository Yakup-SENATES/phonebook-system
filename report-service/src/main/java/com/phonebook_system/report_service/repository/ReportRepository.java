package com.phonebook_system.report_service.repository;

import com.phonebook_system.report_service.entity.ReportEntity;
import com.phonebook_system.report_service.model.ReportStatus;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    @Query("SELECT r FROM ReportEntity r WHERE r.status <> 'COMPLETED'")
    Optional<ReportEntity> findByStatusNotCompleted();

    List<ReportEntity> findByStatus(ReportStatus status);

    @EntityGraph(attributePaths = "details")
    Optional<ReportEntity> findWithDetailsById(UUID id);
}
