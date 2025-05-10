-- Criação da tabela de países
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    iso3 VARCHAR(3),
    numeric_code VARCHAR(3),
    iso2 VARCHAR(2),
    phonecode VARCHAR(255),
    capital VARCHAR(255),
    currency VARCHAR(255),
    currency_name VARCHAR(255),
    currency_symbol VARCHAR(255),
    tld VARCHAR(255),
    native_name VARCHAR(255),
    region VARCHAR(255),
    subregion VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    emoji VARCHAR(255),
    emojiU VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    flag BOOLEAN DEFAULT 1,
    wikiDataId VARCHAR(255)
);

-- Criação da tabela de estados
CREATE TABLE IF NOT EXISTS states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country_id BIGINT NOT NULL,
    country_code VARCHAR(2),
    state_code VARCHAR(255),
    type VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    flag BOOLEAN DEFAULT 1,
    wikiDataId VARCHAR(255),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- Criação da tabela de cidades
CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    state_id BIGINT NOT NULL,
    state_code VARCHAR(255),
    country_id BIGINT NOT NULL,
    country_code VARCHAR(2),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    flag BOOLEAN DEFAULT 1,
    wikiDataId VARCHAR(255),
    FOREIGN KEY (state_id) REFERENCES states(id),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- Criação de índices para melhorar a performance das consultas
CREATE INDEX idx_countries_iso2 ON countries(iso2);
CREATE INDEX idx_states_country_id ON states(country_id);
CREATE INDEX idx_states_country_code ON states(country_code);
CREATE INDEX idx_cities_state_id ON cities(state_id);
CREATE INDEX idx_cities_country_id ON cities(country_id);
