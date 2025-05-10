CREATE TABLE produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    imagem VARCHAR(255),
    codigo_sku VARCHAR(255),
    estoque INT,
    categoria VARCHAR(50),
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao DATETIME,
    data_atualizacao DATETIME,
    moeda VARCHAR(10),
    proprietario_id BIGINT NOT NULL,
    FOREIGN KEY (proprietario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
