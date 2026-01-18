package com.phonebook_system.report_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "t_report_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    private String location;
    private Long personCount;
    private Long phoneNumberCount;
}
