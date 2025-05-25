-- Criação da tabela regions
CREATE TABLE IF NOT EXISTS regions (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    wiki_data_id VARCHAR(100)
);
