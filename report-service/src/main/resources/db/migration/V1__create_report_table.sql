-- V1: Create report table

CREATE TABLE IF NOT EXISTS t_report (
    id UUID PRIMARY KEY,
    request_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE INDEX idx_report_status ON t_report(status);
