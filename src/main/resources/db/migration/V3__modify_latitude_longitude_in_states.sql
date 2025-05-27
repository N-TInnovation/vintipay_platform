-- Altera os campos latitude e longitude na tabela states para VARCHAR(50)
ALTER TABLE states CHANGE COLUMN latitude latitude VARCHAR(50);
ALTER TABLE states CHANGE COLUMN longitude longitude VARCHAR(50);
