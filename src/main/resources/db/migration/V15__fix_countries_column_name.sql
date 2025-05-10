-- Adiciona a coluna wiki_data_id à tabela countries
-- A aplicação está tentando usar wiki_data_id, mas a coluna criada foi wikiDataId
ALTER TABLE countries ADD COLUMN wiki_data_id VARCHAR(255) DEFAULT NULL;
