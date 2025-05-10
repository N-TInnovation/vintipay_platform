-- Criação da tabela de contato da conta de pagamento
CREATE TABLE contato_conta_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cc VARCHAR(255),
    subscriber VARCHAR(255)
);
