-- V2: Create report detail table

CREATE TABLE IF NOT EXISTS t_report_detail (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL,
    location VARCHAR(255),
    person_count BIGINT,
    phone_number_count BIGINT,
    CONSTRAINT fk_report_detail_report
        FOREIGN KEY (report_id)
        REFERENCES t_report(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_report_detail_report_id ON t_report_detail(report_id);
CREATE INDEX idx_report_detail_location ON t_report_detail(location);
