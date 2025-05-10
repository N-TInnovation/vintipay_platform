-- Criação da tabela de pagamento
CREATE TABLE pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intencao_pagamento_id BIGINT NOT NULL,
    fingerprint VARCHAR(255) NOT NULL,
    result_fingerprint VARCHAR(255),
    timestamp VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    mensagem VARCHAR(255),
    reference VARCHAR(255) UNIQUE,
    pan_mascarado VARCHAR(255),
    authorization_code TEXT,
    created_at TIMESTAMP NOT NULL,
    metodo_pagamento_id BIGINT,
    CONSTRAINT fk_pagamento_intencao FOREIGN KEY (intencao_pagamento_id) REFERENCES intencao_pagamento(id)
);
