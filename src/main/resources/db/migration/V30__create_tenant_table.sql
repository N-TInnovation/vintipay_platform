-- Criação da tabela tenant
CREATE TABLE IF NOT EXISTS tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    documento VARCHAR(50),
    email VARCHAR(100),
    telefone VARCHAR(20),
    endereco TEXT,
    cidade VARCHAR(100),
    pais VARCHAR(100),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    logo_url VARCHAR(500),
    configuracoes TEXT,
    observacoes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT UK_TENANT_CODIGO UNIQUE (codigo),
    CONSTRAINT UK_TENANT_SLUG UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Criar tabela de junção usuario_tenant (relacionamento ManyToMany)
CREATE TABLE IF NOT EXISTS usuario_tenant (
    usuario_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, tenant_id),
    CONSTRAINT FK_USUARIO_TENANT_USUARIO FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT FK_USUARIO_TENANT_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Adicionar coluna tenant_id na tabela configuracao_pagamento
ALTER TABLE configuracao_pagamento
ADD COLUMN tenant_id BIGINT NOT NULL AFTER usuario_id,
ADD CONSTRAINT FK_CONFIGURACAO_PAGAMENTO_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- Adicionar coluna tenant_id na tabela produtos
ALTER TABLE produtos
ADD COLUMN tenant_id BIGINT NOT NULL AFTER proprietario_id,
ADD CONSTRAINT FK_PRODUTOS_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- Adicionar coluna tenant_id na tabela links_pagamento
ALTER TABLE links_pagamento
ADD COLUMN tenant_id BIGINT NOT NULL AFTER proprietario_id,
ADD CONSTRAINT FK_LINKS_PAGAMENTO_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- Adicionar coluna tenant_id na tabela planos
ALTER TABLE planos
ADD COLUMN tenant_id BIGINT NOT NULL AFTER ativo,
ADD CONSTRAINT FK_PLANOS_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- Criar índices para melhor performance nas consultas por tenant
CREATE INDEX idx_usuario_tenant_usuario_id ON usuario_tenant(usuario_id);
CREATE INDEX idx_usuario_tenant_tenant_id ON usuario_tenant(tenant_id);
CREATE INDEX idx_configuracao_pagamento_tenant_id ON configuracao_pagamento(tenant_id);
CREATE INDEX idx_produtos_tenant_id ON produtos(tenant_id);
CREATE INDEX idx_links_pagamento_tenant_id ON links_pagamento(tenant_id);
CREATE INDEX idx_planos_tenant_id ON planos(tenant_id);
