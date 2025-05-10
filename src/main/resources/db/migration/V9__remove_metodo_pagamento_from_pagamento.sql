-- Primeiro modifica a coluna para aceitar NULL
ALTER TABLE pagamento MODIFY COLUMN metodo_pagamento_id bigint NULL;

-- Remove a chave estrangeira se existir
-- MySQL não suporta IF EXISTS para DROP FOREIGN KEY diretamente
-- Verifica se a constraint existe antes de tentar removê-la
SET @constraint_name = 'FKbu9f4kq6sa73svk60pyyur0gi';
SET @query = (SELECT IF(
    EXISTS(
        SELECT * FROM information_schema.table_constraints 
        WHERE constraint_name = @constraint_name 
        AND table_name = 'pagamento'
    ),
    CONCAT('ALTER TABLE pagamento DROP FOREIGN KEY ', @constraint_name, ';'),
    'SELECT 1;'
));

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Por fim remove a coluna
ALTER TABLE pagamento DROP COLUMN metodo_pagamento_id;
