-- Altera os campos latitude e longitude na tabela states para VARCHAR(50)
ALTER TABLE states MODIFY COLUMN latitude VARCHAR(50);
ALTER TABLE states MODIFY COLUMN longitude VARCHAR(50);
