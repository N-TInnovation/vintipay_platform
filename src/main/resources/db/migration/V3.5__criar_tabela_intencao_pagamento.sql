-- Criação da tabela de intenção de pagamento
CREATE TABLE intencao_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conta_pagamento_id BIGINT NOT NULL,
    merchant_ref VARCHAR(255) NOT NULL,
    merchant_session VARCHAR(255) NOT NULL,
    pos_id VARCHAR(255) NOT NULL,
    timestamp VARCHAR(255) NOT NULL,
    entity_code VARCHAR(255),
    reference_number BIGINT,
    amount DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50),
    reference VARCHAR(255) UNIQUE,
    currency VARCHAR(10),
    transaction_code VARCHAR(255),
    CONSTRAINT fk_intencao_conta FOREIGN KEY (conta_pagamento_id) REFERENCES conta_pagamento(id)
);
