-- Alterar tabela cv_locations para incluir todos os campos do CSV
ALTER TABLE cv_locations 
    ADD COLUMN IF NOT EXISTS codigo VARCHAR(20) UNIQUE,
    ADD COLUMN IF NOT EXISTS nivel INT,
    ADD COLUMN IF NOT EXISTS pais VARCHAR(10);

-- Renomear colunas para usar os nomes em português conforme o CSV
ALTER TABLE cv_locations 
    CHANGE COLUMN island ilha VARCHAR(255),
    CHANGE COLUMN county concelho VARCHAR(255),
    CHANGE COLUMN parish freguesia VARCHAR(255),
    CHANGE COLUMN zone zona VARCHAR(255),
    CHANGE COLUMN subzone lugar VARCHAR(255),
    CHANGE COLUMN name nome VARCHAR(255);
