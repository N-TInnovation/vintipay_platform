-- Script para inserir papéis padrão se não existirem
INSERT INTO papeis (nome, descricao, codigo, padrao, created_at, updated_at)
SELECT 'USER', 'Usuário padrão do sistema', 'USER', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM papeis WHERE codigo = 'USER');

INSERT INTO papeis (nome, descricao, codigo, padrao, created_at, updated_at)
SELECT 'ADMIN', 'Administrador do sistema', 'ADMIN', false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM papeis WHERE codigo = 'ADMIN');

INSERT INTO papeis (nome, descricao, codigo, padrao, created_at, updated_at)
SELECT 'MANAGER', 'Gerente do sistema', 'MANAGER', false, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM papeis WHERE codigo = 'MANAGER');
