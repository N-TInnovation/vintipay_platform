DROP TABLE IF EXISTS cv_locations;
-- Primeiro, criar a tabela se não existir
CREATE TABLE IF NOT EXISTS cv_locations (
    id BIGINT PRIMARY KEY,
    codigo VARCHAR(20),
    nivel INT,
    pais VARCHAR(10),
    ilha VARCHAR(255),
    concelho VARCHAR(255),
    freguesia VARCHAR(255),
    zona VARCHAR(255),
    lugar VARCHAR(255),
    nome VARCHAR(255),
    lat VARCHAR(20),
    lng VARCHAR(20)
);