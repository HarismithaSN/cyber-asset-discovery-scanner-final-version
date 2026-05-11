CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    action VARCHAR(50),
    details TEXT,
    username VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);