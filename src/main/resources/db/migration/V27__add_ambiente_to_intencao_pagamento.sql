-- Adicionar coluna ambiente à tabela intencao_pagamento
ALTER TABLE intencao_pagamento ADD COLUMN ambiente VARCHAR(20) NOT NULL DEFAULT 'DESENVOLVIMENTO';

-- Atualizar registros existentes (opcional)
UPDATE intencao_pagamento SET ambiente = 'DESENVOLVIMENTO' WHERE ambiente IS NULL;
