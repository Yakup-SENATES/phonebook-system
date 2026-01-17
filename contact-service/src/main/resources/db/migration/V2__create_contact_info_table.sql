CREATE TABLE contact_info (
    id UUID PRIMARY KEY,
    person_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    content VARCHAR(255) NOT NULL,
    CONSTRAINT fk_person
        FOREIGN KEY (person_id)
        REFERENCES person(id)
        ON DELETE CASCADE -- Kiþi silinirse iletiþim bilgileri de silinir
);
