-- Adiciona as colunas ausentes à tabela states
ALTER TABLE states ADD COLUMN state_code VARCHAR(255) DEFAULT NULL;
ALTER TABLE states ADD COLUMN wiki_data_id VARCHAR(255) DEFAULT NULL;
