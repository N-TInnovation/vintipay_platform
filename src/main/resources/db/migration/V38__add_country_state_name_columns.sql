-- Adiciona a coluna country_name à tabela states
ALTER TABLE states ADD COLUMN country_name VARCHAR(255);

-- Adiciona as colunas state_name e country_name à tabela cities
ALTER TABLE cities ADD COLUMN state_name VARCHAR(255);
ALTER TABLE cities ADD COLUMN country_name VARCHAR(255);
