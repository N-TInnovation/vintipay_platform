-- Primeiro modifica a coluna para aceitar NULL
ALTER TABLE pagamento MODIFY COLUMN metodo_pagamento_id bigint NULL;

-- Remove a chave estrangeira se existir
ALTER TABLE pagamento DROP FOREIGN KEY IF EXISTS FKbu9f4kq6sa73svk60pyyur0gi;

-- Por fim remove a coluna
ALTER TABLE pagamento DROP COLUMN metodo_pagamento_id;
