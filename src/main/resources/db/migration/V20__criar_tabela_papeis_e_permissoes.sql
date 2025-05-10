-- Criação da tabela de papéis
CREATE TABLE IF NOT EXISTS papeis (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(255) NOT NULL UNIQUE,
  descricao varchar(500),
  codigo varchar(255) NOT NULL UNIQUE,
  padrao boolean NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id)
);

-- Criação da tabela de permissões
CREATE TABLE IF NOT EXISTS permissoes (
  id bigint NOT NULL AUTO_INCREMENT,
  nome varchar(255) NOT NULL UNIQUE,
  descricao varchar(500),
  codigo varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);

-- Criação da tabela de relacionamento papel-permissão
CREATE TABLE IF NOT EXISTS papel_permissao (
  papel_id bigint NOT NULL,
  permissao_id bigint NOT NULL,
  PRIMARY KEY (papel_id, permissao_id),
  CONSTRAINT FK_papel_permissao_papel FOREIGN KEY (papel_id) REFERENCES papeis (id),
  CONSTRAINT FK_papel_permissao_permissao FOREIGN KEY (permissao_id) REFERENCES permissoes (id)
);

-- Criação da tabela de relacionamento usuário-papel
CREATE TABLE IF NOT EXISTS usuario_papel (
  usuario_id bigint NOT NULL,
  papel_id bigint NOT NULL,
  PRIMARY KEY (usuario_id, papel_id),
  CONSTRAINT FK_usuario_papel_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  CONSTRAINT FK_usuario_papel_papel FOREIGN KEY (papel_id) REFERENCES papeis (id)
);

-- Inserir papel ADMIN padrão
INSERT INTO papeis (nome, descricao, codigo, padrao) 
VALUES ('Administrador', 'Acesso total ao sistema', 'ADMIN', TRUE);
