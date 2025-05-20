-- Adicionar colunas para recuperação de senha à tabela de usuários
ALTER TABLE usuarios
    ADD COLUMN reset_password_token VARCHAR(255) NULL,
    ADD COLUMN reset_password_expires DATETIME NULL;

-- Adicionar índice para melhorar a performance de consultas por token
CREATE INDEX idx_usuarios_reset_password_token ON usuarios (reset_password_token);
