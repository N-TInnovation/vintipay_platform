-- Adicionar colunas region_id e subregion_id à tabela countries
ALTER TABLE countries ADD COLUMN region_id BIGINT;
ALTER TABLE countries ADD COLUMN subregion_id BIGINT;

-- Adicionar chaves estrangeiras
ALTER TABLE countries ADD CONSTRAINT fk_country_region 
    FOREIGN KEY (region_id) REFERENCES regions(id);
    
ALTER TABLE countries ADD CONSTRAINT fk_country_subregion 
    FOREIGN KEY (subregion_id) REFERENCES subregions(id);
