-- Modificar a estrutura da tabela para tornar terminal_id e service_url opcionais
ALTER TABLE configuracao_pagamento MODIFY terminal_id VARCHAR(255) NULL DEFAULT '';
ALTER TABLE configuracao_pagamento MODIFY service_url VARCHAR(255) NULL DEFAULT '';

-- Atualizar registros existentes para definir valores padrão
UPDATE configuracao_pagamento SET terminal_id = '' WHERE terminal_id IS NULL;
UPDATE configuracao_pagamento SET service_url = '' WHERE service_url IS NULL;
