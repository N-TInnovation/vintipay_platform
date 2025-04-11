-- Limpa dados existentes
DELETE FROM configuracao_pagamento;
DELETE FROM metodo_pagamento;

-- Insere método de pagamento
INSERT INTO metodo_pagamento (name) VALUES ('CARTAO_CREDITO');
