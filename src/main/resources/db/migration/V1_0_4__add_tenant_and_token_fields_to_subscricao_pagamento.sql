-- Adicionar tenant_id e campos de token à tabela subscricao_pagamento

-- Adicionar coluna tenant_id
ALTER TABLE subscricao_pagamento ADD COLUMN tenant_id BIGINT;

-- Adicionar foreign key para tenant
ALTER TABLE subscricao_pagamento 
ADD CONSTRAINT fk_subscricao_pagamento_tenant 
FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- Adicionar índice para melhor performance
CREATE INDEX idx_subscricao_pagamento_tenant_id ON subscricao_pagamento(tenant_id);

-- Adicionar campos de informações do token
ALTER TABLE subscricao_pagamento ADD COLUMN token_description VARCHAR(255);
ALTER TABLE subscricao_pagamento ADD COLUMN max_number_of_transactions INT;
ALTER TABLE subscricao_pagamento ADD COLUMN token_expiry_date VARCHAR(50);
ALTER TABLE subscricao_pagamento ADD COLUMN max_amount_allowed VARCHAR(50);
ALTER TABLE subscricao_pagamento ADD COLUMN limit_date VARCHAR(50);
