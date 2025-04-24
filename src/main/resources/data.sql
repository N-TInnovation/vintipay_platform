-- Insere método de pagamento (apenas se não existir)
INSERT INTO metodo_pagamento (name) VALUES ('CARTAO_CREDITO') ON DUPLICATE KEY UPDATE name=name;
