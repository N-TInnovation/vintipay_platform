-- Criação da tabela metodo_pagamento se não existir
CREATE TABLE IF NOT EXISTS metodo_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Criação da tabela produtos se não existir
CREATE TABLE IF NOT EXISTS produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL,
    imagem VARCHAR(1024),
    codigo_sku VARCHAR(255),
    estoque INT,
    categoria VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    moeda VARCHAR(3) DEFAULT 'CVE',
    proprietario_id BIGINT NOT NULL,
    FOREIGN KEY (proprietario_id) REFERENCES usuarios(id)
);

-- Criação da tabela links_pagamento se não existir
CREATE TABLE IF NOT EXISTS links_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    titulo VARCHAR(255),
    descricao TEXT,
    status VARCHAR(50) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_expiracao TIMESTAMP,
    quantidade_usos INT,
    usos INT DEFAULT 0,
    produto_id BIGINT NOT NULL,
    proprietario_id BIGINT NOT NULL,
    url_retorno_sucesso VARCHAR(1024),
    url_retorno_cancelamento VARCHAR(1024),
    FOREIGN KEY (produto_id) REFERENCES produtos(id),
    FOREIGN KEY (proprietario_id) REFERENCES usuarios(id)
);

-- Índices para melhorar a performance das consultas
CREATE INDEX IF NOT EXISTS idx_produtos_proprietario ON produtos(proprietario_id);
CREATE INDEX IF NOT EXISTS idx_links_pagamento_produto ON links_pagamento(produto_id);
CREATE INDEX IF NOT EXISTS idx_links_pagamento_proprietario ON links_pagamento(proprietario_id);
CREATE INDEX IF NOT EXISTS idx_links_pagamento_codigo ON links_pagamento(codigo);