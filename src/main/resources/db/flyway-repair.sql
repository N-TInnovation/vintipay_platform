-- Script SQL para limpar a tabela de histórico do Flyway
-- Este script deve ser executado manualmente no banco de dados do Render

-- Opção 1: Marcar a migração V37 como bem-sucedida
UPDATE flyway_schema_history 
SET success = 1 
WHERE version = '37' AND success = 0;

-- Opção 2 (mais radical): Remover completamente a migração V37 do histórico
-- DELETE FROM flyway_schema_history WHERE version = '37';

-- Opção 3 (mais extrema): Limpar todo o histórico de migrações 
-- (use com cuidado, isso fará o Flyway aplicar todas as migrações novamente)
-- DELETE FROM flyway_schema_history;
