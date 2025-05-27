-- Script para recriar as tabelas principais após apagar o banco de dados

-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    papel VARCHAR(50),
    email_verificado BOOLEAN DEFAULT FALSE,
    token_verificacao VARCHAR(255),
    token_expiracao TIMESTAMP,
    telefone VARCHAR(50),
    endereco TEXT,
    foto_perfil VARCHAR(1024),
    ultimo_acesso TIMESTAMP,
    cliente_id VARCHAR(255),
    assinatura_id VARCHAR(255),
    assinatura_status VARCHAR(50)
);

-- Criação da tabela countries
CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    iso3 VARCHAR(3) UNIQUE,
    numeric_code VARCHAR(3) UNIQUE,
    iso2 VARCHAR(2) UNIQUE,
    phonecode VARCHAR(255),
    capital VARCHAR(255),
    currency VARCHAR(255),
    currency_name VARCHAR(255),
    currency_symbol VARCHAR(255),
    tld VARCHAR(255),
    native_name VARCHAR(255),
    region VARCHAR(255),
    subregion VARCHAR(255),
    emoji VARCHAR(255),
    emojiU VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    flag TINYINT(1),
    wikiDataId VARCHAR(255),
    timezones TEXT
);

-- Criação da tabela states
CREATE TABLE IF NOT EXISTS states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country_id BIGINT NOT NULL,
    country_code VARCHAR(2),
    country_name VARCHAR(255),
    state_code VARCHAR(255),
    type VARCHAR(255),
    latitude VARCHAR(50),
    longitude VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- Criação da tabela cities
CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    state_id BIGINT,
    country_id BIGINT NOT NULL,
    country_code VARCHAR(2),
    state_code VARCHAR(255),
    latitude VARCHAR(50),
    longitude VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (state_id) REFERENCES states(id),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- Tabela de métodos de pagamento
CREATE TABLE IF NOT EXISTS metodo_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Tabela de produtos
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

-- Tabela de links de pagamento
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

-- Adicione aqui outras tabelas que sua aplicação precisa
