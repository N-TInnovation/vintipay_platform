-- Adiciona coluna tenant_id na tabela intencao_pagamento
ALTER TABLE intencao_pagamento
ADD COLUMN tenant_id BIGINT NOT NULL,
ADD CONSTRAINT fk_intencao_pagamento_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenant(id)
    ON DELETE RESTRICT;

-- Criar índice para melhorar performance nas consultas por tenant
CREATE INDEX idx_intencao_pagamento_tenant_id ON intencao_pagamento(tenant_id);
