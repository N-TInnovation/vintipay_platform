-- Recriação da tabela cv_locations com estrutura completa
-- DROP da tabela se já existir para evitar conflitos
DROP TABLE IF EXISTS cv_locations;

-- Criar a tabela com a estrutura correta
CREATE TABLE cv_locations (
    id BIGINT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE,
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

-- Criar índices para melhor performance
CREATE INDEX idx_cv_locations_ilha ON cv_locations(ilha);
CREATE INDEX idx_cv_locations_concelho ON cv_locations(concelho);
CREATE INDEX idx_cv_locations_zona ON cv_locations(zona);
