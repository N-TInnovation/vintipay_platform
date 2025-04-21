ALTER TABLE configuracao_pagamento 
ADD COLUMN nome_loja VARCHAR(255) NOT NULL DEFAULT 'Loja Padrão',
ADD COLUMN url_loja VARCHAR(255) NOT NULL DEFAULT 'https://example.com',
ADD COLUMN telefone VARCHAR(50),
ADD COLUMN email VARCHAR(255);

-- Remover valores padrão após a migração inicial
ALTER TABLE configuracao_pagamento 
ALTER COLUMN nome_loja DROP DEFAULT,
ALTER COLUMN url_loja DROP DEFAULT;
