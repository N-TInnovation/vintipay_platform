-- Adiciona coluna tenant_id na tabela pagamento
ALTER TABLE pagamento
ADD COLUMN tenant_id BIGINT NOT NULL,
ADD CONSTRAINT fk_pagamento_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenant(id)
    ON DELETE RESTRICT;

-- Criar índice para melhorar performance nas consultas por tenant
CREATE INDEX idx_pagamento_tenant_id ON pagamento(tenant_id);
