-- Criação da tabela metodo_pagamento se não existir
CREATE TABLE IF NOT EXISTS metodo_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);