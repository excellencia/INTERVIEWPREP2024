-- Normalized tables with proper relationships

-- Vehicles table (stores vehicle information)
CREATE TABLE vehicles (
    license_plate VARCHAR(20) PRIMARY KEY,
    vehicle_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Parking spots table (stores static spot information)
CREATE TABLE parking_spots (
    spot_id INT PRIMARY KEY,
    level_number INT NOT NULL,
    position_number INT NOT NULL,
    spot_status VARCHAR(20) DEFAULT 'AVAILABLE',
    CONSTRAINT unique_spot_location UNIQUE (level_number, position_number)
);

-- Parking sessions table (stores active and historical parking information)
CREATE TABLE parking_sessions (
    session_id UUID PRIMARY KEY,
    license_plate VARCHAR(20) REFERENCES vehicles(license_plate),
    spot_id INT REFERENCES parking_spots(spot_id),
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_exit_time CHECK (exit_time IS NULL OR exit_time > entry_time)
);

-- Parking tickets table (stores ticket information)
CREATE TABLE parking_tickets (
    ticket_id UUID PRIMARY KEY,
    session_id UUID REFERENCES parking_sessions(session_id),
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Indexes for better query performance
CREATE INDEX idx_parking_sessions_status ON parking_sessions(status);
CREATE INDEX idx_parking_sessions_license_plate ON parking_sessions(license_plate);
CREATE INDEX idx_parking_tickets_session_id ON parking_tickets(session_id);