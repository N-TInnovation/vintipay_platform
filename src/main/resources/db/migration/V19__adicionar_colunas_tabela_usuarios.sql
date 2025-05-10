-- Adiciona as colunas faltantes à tabela usuarios
ALTER TABLE usuarios ADD COLUMN data_registro datetime DEFAULT NULL;
ALTER TABLE usuarios ADD COLUMN ultimo_login datetime DEFAULT NULL;
ALTER TABLE usuarios ADD COLUMN stripe_customer_id varchar(255) DEFAULT NULL;
ALTER TABLE usuarios ADD COLUMN nome varchar(255) DEFAULT NULL;
ALTER TABLE usuarios ADD COLUMN telefone varchar(255) DEFAULT NULL;
ALTER TABLE usuarios ADD COLUMN email varchar(255) DEFAULT NULL;
