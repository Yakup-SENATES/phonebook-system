package com.phonebook_system.contact_service.entity;

import com.phonebook_system.contact_service.model.ContactTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "t_contact_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
 * Person (1) → ContactInfo (N)
 */
public class ContactInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private PersonEntity person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactTypeEnum type;

    @Column(nullable = false)
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactInfoEntity that)) return false;
        return Objects.equals(id, that.id) && type == that.type;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
