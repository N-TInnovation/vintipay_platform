CREATE TABLE links_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    titulo VARCHAR(255),
    descricao TEXT,
    status VARCHAR(50) NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_expiracao DATETIME,
    quantidade_usos INT,
    usos INT,
    produto_id BIGINT NOT NULL,
    configuracao_pagamento_id BIGINT,
    proprietario_id BIGINT NOT NULL,
    url_retorno_sucesso VARCHAR(255),
    url_retorno_cancelamento VARCHAR(255),
    FOREIGN KEY (produto_id) REFERENCES produtos(id),
    FOREIGN KEY (configuracao_pagamento_id) REFERENCES configuracao_pagamento(id),
    FOREIGN KEY (proprietario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
