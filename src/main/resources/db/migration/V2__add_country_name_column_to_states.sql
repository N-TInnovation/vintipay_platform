-- Adiciona a coluna country_name à tabela states se ela não existir
ALTER TABLE states ADD COLUMN IF NOT EXISTS country_name VARCHAR(255);
