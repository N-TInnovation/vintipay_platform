-- Criação da tabela subregions
CREATE TABLE IF NOT EXISTS subregions (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    region_id BIGINT NOT NULL,
    wiki_data_id VARCHAR(100),
    CONSTRAINT fk_subregion_region FOREIGN KEY (region_id) REFERENCES regions(id)
);
