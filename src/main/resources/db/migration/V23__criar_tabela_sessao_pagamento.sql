-- Criação da tabela de sessão de pagamento
CREATE TABLE IF NOT EXISTS sessao_pagamento (
  id bigint NOT NULL AUTO_INCREMENT,
  session_id varchar(255) NOT NULL UNIQUE,
  reference varchar(255) NOT NULL,
  client_id varchar(255) NOT NULL,
  ip_address varchar(255),
  user_agent varchar(1000),
  created_at datetime NOT NULL,
  expires_at datetime NOT NULL,
  valid boolean NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id)
);
