-- Criação da tabela de configuração de aplicação (integrações)
CREATE TABLE configuracao_aplicacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    base_url VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    api_secret VARCHAR(255) NOT NULL,
    usuario_id BIGINT,
    configuracao_pagamento_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_app_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_configuracao_pagamento FOREIGN KEY (configuracao_pagamento_id) REFERENCES configuracao_pagamento(id)
);
