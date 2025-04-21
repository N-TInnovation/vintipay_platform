DROP TABLE IF EXISTS configuracao_pagamento;
DROP TABLE IF EXISTS metodo_pagamento;

CREATE TABLE metodo_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE configuracao_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pos_id VARCHAR(255) NOT NULL,
    merchant_id VARCHAR(255) NOT NULL,
    terminal_id VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    api_secret VARCHAR(255) NOT NULL,
    pos_aut_code VARCHAR(255) NOT NULL,
    return_aut_code VARCHAR(255) NOT NULL,
    return_url VARCHAR(255) NOT NULL,
    nome_loja VARCHAR(255) NOT NULL,
    url_loja VARCHAR(255) NOT NULL,
    telefone VARCHAR(50),
    email VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL
);
