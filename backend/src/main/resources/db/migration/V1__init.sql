CREATE TABLE assets (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    ip_address VARCHAR(50),
    domain VARCHAR(255),
    status VARCHAR(50),
    risk_score INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_assets_name ON assets(name);
CREATE INDEX idx_assets_ip ON assets(ip_address);
CREATE INDEX idx_assets_status ON assets(status);