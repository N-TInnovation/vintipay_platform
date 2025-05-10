-- Adiciona a coluna wiki_data_id à tabela cities
ALTER TABLE cities ADD COLUMN wiki_data_id VARCHAR(255) DEFAULT NULL;
