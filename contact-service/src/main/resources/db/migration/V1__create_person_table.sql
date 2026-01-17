-- V1: Create person table

CREATE TABLE IF NOT EXISTS t_person (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    company VARCHAR(255)
);

CREATE INDEX idx_person_first_name ON t_person(first_name);
CREATE INDEX idx_person_last_name ON t_person(last_name);
CREATE INDEX idx_person_company ON t_person(company);
