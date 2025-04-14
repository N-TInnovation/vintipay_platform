-- Primeiro modifica a coluna para aceitar NULL
ALTER TABLE intencao_pagamento MODIFY COLUMN metodo_pagamento_id bigint NULL;

-- Remove a chave estrangeira
ALTER TABLE intencao_pagamento DROP FOREIGN KEY FKbu9f4kq6sa73svk60pyyur0gi;

-- Por fim remove a coluna
ALTER TABLE intencao_pagamento DROP COLUMN metodo_pagamento_id;
