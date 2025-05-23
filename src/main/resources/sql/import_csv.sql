-- Script para importar dados CSV diretamente no MySQL
-- Criado em: 2025-05-23

-- Configurações para importação de CSV
SET GLOBAL local_infile = 1;
SET SQL_MODE = '';

-- Criar tabelas se não existirem
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    iso3 VARCHAR(3) NOT NULL,
    iso2 VARCHAR(2) NOT NULL,
    numeric_code VARCHAR(10),
    phonecode VARCHAR(10),
    capital VARCHAR(100),
    currency VARCHAR(5),
    currency_name VARCHAR(100),
    currency_symbol VARCHAR(10),
    tld VARCHAR(10),
    native_name VARCHAR(100),
    region VARCHAR(100),
    region_id INT,
    subregion VARCHAR(100),
    subregion_id INT,
    nationality VARCHAR(100),
    timezones TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    emoji VARCHAR(10),
    emojiU VARCHAR(30),
    active BOOLEAN DEFAULT TRUE,
    UNIQUE KEY uk_countries_iso2 (iso2)
);

CREATE TABLE IF NOT EXISTS states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country_id BIGINT,
    country_code VARCHAR(2),
    state_code VARCHAR(10) NOT NULL,
    type VARCHAR(30),
    latitude VARCHAR(30),
    longitude VARCHAR(30),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT,
    country_id BIGINT,
    country_code VARCHAR(2),
    state_code VARCHAR(10),
    latitude VARCHAR(30),
    longitude VARCHAR(30),
    FOREIGN KEY (state_id) REFERENCES states(id),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE IF NOT EXISTS regions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    wikiDataId VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS subregions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region_id INT,
    wikiDataId VARCHAR(100),
    FOREIGN KEY (region_id) REFERENCES regions(id)
);

-- Limpar dados existentes
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE cities;
TRUNCATE TABLE states;
TRUNCATE TABLE countries;
TRUNCATE TABLE subregions;
TRUNCATE TABLE regions;
SET FOREIGN_KEY_CHECKS = 1;

-- Importar dados de regions
LOAD DATA LOCAL INFILE '/home/natalio/IdeaProjects/vintipay/src/main/resources/csv/regions.csv'
INTO TABLE regions
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(id, name, wikiDataId);

-- Importar dados de subregions
LOAD DATA LOCAL INFILE '/home/natalio/IdeaProjects/vintipay/src/main/resources/csv/subregions.csv'
INTO TABLE subregions
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(id, name, region_id, wikiDataId);

-- Importar dados de countries
LOAD DATA LOCAL INFILE '/home/natalio/IdeaProjects/vintipay/src/main/resources/csv/countries.csv'
INTO TABLE countries
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(id, name, iso3, iso2, numeric_code, phonecode, capital, currency, 
currency_name, currency_symbol, tld, native_name, region, region_id, 
subregion, subregion_id, nationality, timezones, latitude, longitude, 
emoji, emojiU);

-- Importar dados de states
LOAD DATA LOCAL INFILE '/home/natalio/IdeaProjects/vintipay/src/main/resources/csv/states.csv'
INTO TABLE states
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(id, name, country_id, country_code, state_code, type, latitude, longitude);

-- Importar dados de cities (apenas para BR, PT, CV - já que o arquivo é muito grande)
LOAD DATA LOCAL INFILE '/home/natalio/IdeaProjects/vintipay/src/main/resources/csv/cities.csv'
INTO TABLE cities
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(id, name, state_id, state_code, country_id, country_code, latitude, longitude, @dummy, @dummy)
WHERE country_code IN ('BR', 'PT', 'CV');

-- Atualizando flags para ativar países relevantes
UPDATE countries SET active = TRUE WHERE iso2 IN ('BR', 'PT', 'CV');

-- Verificar importação
SELECT 'Contagem de regiões importadas:' AS info, COUNT(*) AS count FROM regions
UNION ALL
SELECT 'Contagem de sub-regiões importadas:', COUNT(*) FROM subregions
UNION ALL
SELECT 'Contagem de países importados:', COUNT(*) FROM countries
UNION ALL
SELECT 'Contagem de estados importados:', COUNT(*) FROM states
UNION ALL
SELECT 'Contagem de cidades importadas:', COUNT(*) FROM cities
UNION ALL
SELECT 'Contagem de cidades brasileiras:', COUNT(*) FROM cities c JOIN countries co ON c.country_id = co.id WHERE co.iso2 = 'BR'
UNION ALL
SELECT 'Contagem de cidades portuguesas:', COUNT(*) FROM cities c JOIN countries co ON c.country_id = co.id WHERE co.iso2 = 'PT'
UNION ALL
SELECT 'Contagem de cidades cabo-verdianas:', COUNT(*) FROM cities c JOIN countries co ON c.country_id = co.id WHERE co.iso2 = 'CV';
