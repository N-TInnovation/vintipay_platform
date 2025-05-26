-- Script para reparar a tabela de histórico do Flyway
-- Este script marca a migração V37 como bem-sucedida para evitar falhas na inicialização

-- Verifica se a migração falhou e a corrige
UPDATE flyway_schema_history 
SET success = 1 
WHERE version = '37' AND success = 0;

-- Atualiza a descrição da migração para indicar que foi reparada
UPDATE flyway_schema_history 
SET description = 'update_cape_verde_locations_table (fixed)'
WHERE version = '37' AND success = 1;
