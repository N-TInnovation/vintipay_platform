-- Adicionar colunas de auditoria à tabela papeis
ALTER TABLE papeis ADD COLUMN created_at datetime DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE papeis ADD COLUMN updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Adicionar colunas de auditoria à tabela permissoes
ALTER TABLE permissoes ADD COLUMN created_at datetime DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE permissoes ADD COLUMN updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
