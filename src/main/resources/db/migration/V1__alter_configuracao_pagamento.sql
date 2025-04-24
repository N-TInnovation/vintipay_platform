-- Modificar a tabela configuracao_pagamento para tornar terminal_id e service_url opcionais
ALTER TABLE configuracao_pagamento MODIFY terminal_id VARCHAR(255) DEFAULT '';
ALTER TABLE configuracao_pagamento MODIFY service_url VARCHAR(255) DEFAULT '';

-- Atualizar registros existentes com valores padrão
UPDATE configuracao_pagamento SET terminal_id = '' WHERE terminal_id IS NULL;
UPDATE configuracao_pagamento SET service_url = '' WHERE service_url IS NULL;
