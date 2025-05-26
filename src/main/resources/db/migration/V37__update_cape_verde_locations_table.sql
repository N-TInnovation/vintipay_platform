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

-- Adicionar constraint de unicidade para o código, se ainda não existir
ALTER TABLE cv_locations
    ADD CONSTRAINT IF NOT EXISTS uk_cv_locations_codigo UNIQUE (codigo);

-- Garantir que as colunas existam
ALTER TABLE cv_locations
    ADD COLUMN IF NOT EXISTS codigo VARCHAR(20),
    ADD COLUMN IF NOT EXISTS nivel INT,
    ADD COLUMN IF NOT EXISTS pais VARCHAR(10),
    ADD COLUMN IF NOT EXISTS ilha VARCHAR(255),
    ADD COLUMN IF NOT EXISTS concelho VARCHAR(255),
    ADD COLUMN IF NOT EXISTS freguesia VARCHAR(255),
    ADD COLUMN IF NOT EXISTS zona VARCHAR(255),
    ADD COLUMN IF NOT EXISTS lugar VARCHAR(255),
    ADD COLUMN IF NOT EXISTS nome VARCHAR(255),
    ADD COLUMN IF NOT EXISTS lat VARCHAR(20),
    ADD COLUMN IF NOT EXISTS lng VARCHAR(20);
