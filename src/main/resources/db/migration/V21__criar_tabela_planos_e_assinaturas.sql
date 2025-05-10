-- Criação da tabela de planos
CREATE TABLE IF NOT EXISTS planos (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(255) NOT NULL UNIQUE,
  descricao varchar(1000),
  preco decimal(10,2) NOT NULL,
  periodo_cobranca varchar(20),
  dias_trial int,
  stripe_price_id varchar(255),
  stripe_product_id varchar(255),
  ativo boolean NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id)
);

-- Criação da tabela de assinaturas de planos
CREATE TABLE IF NOT EXISTS planos_assinatura (
  id bigint NOT NULL AUTO_INCREMENT,
  usuario_id bigint NOT NULL,
  plano_id bigint NOT NULL,
  data_inicio datetime,
  data_fim datetime,
  stripe_subscription_id varchar(255),
  status varchar(20),
  ultimo_pagamento datetime,
  proximo_pagamento datetime,
  valor_pago decimal(10,2),
  PRIMARY KEY (id),
  CONSTRAINT FK_plano_assinatura_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  CONSTRAINT FK_plano_assinatura_plano FOREIGN KEY (plano_id) REFERENCES planos (id)
);

-- Inserir um plano padrão
INSERT INTO planos (nome, descricao, preco, periodo_cobranca, dias_trial, ativo)
VALUES ('Básico', 'Plano básico de assinatura', 29.90, 'MENSAL', 7, true);
