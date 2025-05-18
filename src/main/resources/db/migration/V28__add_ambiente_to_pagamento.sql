-- Adicionar coluna ambiente à tabela pagamento
ALTER TABLE pagamento ADD COLUMN ambiente VARCHAR(20) NOT NULL DEFAULT 'DESENVOLVIMENTO';

-- Atualizar registros existentes (opcional)
UPDATE pagamento SET ambiente = 'DESENVOLVIMENTO' WHERE ambiente IS NULL;
