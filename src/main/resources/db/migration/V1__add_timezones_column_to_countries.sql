-- Adiciona a coluna timezones à tabela countries se ela não existir
ALTER TABLE countries ADD COLUMN IF NOT EXISTS timezones TEXT;
