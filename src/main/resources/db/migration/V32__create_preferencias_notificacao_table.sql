-- Criação da tabela de preferências de notificação
CREATE TABLE preferencias_notificacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    email BOOLEAN NOT NULL DEFAULT TRUE,
    sms BOOLEAN NOT NULL DEFAULT TRUE,
    push BOOLEAN NOT NULL DEFAULT FALSE,
    transacoes BOOLEAN NOT NULL DEFAULT TRUE,
    promocoes BOOLEAN NOT NULL DEFAULT FALSE,
    seguranca BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_preferencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT uk_preferencias_usuario UNIQUE (usuario_id)
);

-- Inserir preferências padrão para os usuários existentes
INSERT INTO preferencias_notificacao (usuario_id, email, sms, push, transacoes, promocoes, seguranca)
SELECT id, TRUE, TRUE, FALSE, TRUE, FALSE, TRUE
FROM usuarios;
