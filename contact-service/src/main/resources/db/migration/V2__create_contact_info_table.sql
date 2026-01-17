-- V2: Create contact info table

CREATE TABLE IF NOT EXISTS t_contact_info (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_contact_info_person
        FOREIGN KEY (person_id)
        REFERENCES t_person(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_contact_info_person_id ON t_contact_info(person_id);
CREATE INDEX idx_contact_info_type ON t_contact_info(type);
