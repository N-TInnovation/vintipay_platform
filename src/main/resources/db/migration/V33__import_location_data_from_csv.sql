-- Script para importar dados de localização das tabelas CSV
-- Criado em: 2025-05-24

-- Configurar o modo SQL para permitir a execução sem erros
SET SQL_MODE = '';

-- Desativar verificação de chaves estrangeiras para permitir truncamento
SET FOREIGN_KEY_CHECKS = 0;

-- Limpar tabelas existentes
TRUNCATE TABLE cities;
TRUNCATE TABLE states;
TRUNCATE TABLE countries;

-- Reativar verificação de chaves estrangeiras
SET FOREIGN_KEY_CHECKS = 1;

-- Nota: Este script apenas prepara o banco de dados
-- A importação completa será realizada pelo CsvLocationDataImporter
-- que carregará todos os países, estados e cidades dos arquivos CSV
