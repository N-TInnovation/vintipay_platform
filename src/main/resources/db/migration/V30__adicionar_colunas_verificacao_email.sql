-- Adicionar colunas para verificação de email na tabela usuarios
ALTER TABLE usuarios
ADD COLUMN email_verification_token VARCHAR(255),
ADD COLUMN email_verified BOOLEAN DEFAULT FALSE,
ADD COLUMN email_verification_expires TIMESTAMP;

-- Adicionar índice para busca por token de verificação
CREATE INDEX idx_email_verification_token ON usuarios (email_verification_token);
