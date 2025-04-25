-- Insere método de pagamento (apenas se não existir)
INSERT INTO metodo_pagamento (name) VALUES ('CARTAO_CREDITO') ON DUPLICATE KEY UPDATE name=name;

-- Inserir produtos de exemplo (apenas se a tabela estiver vazia)
INSERT INTO produtos (nome, descricao, preco, imagem, codigo_sku, estoque, categoria, proprietario_id, moeda)
SELECT 'Curso de Programação Java', 'Curso completo de Java para iniciantes e intermediários', 15000.00, 'https://via.placeholder.com/300x200?text=Java+Course', 'JAVA-001', 999, 'Cursos', 1, 'CVE'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM produtos LIMIT 1);

INSERT INTO produtos (nome, descricao, preco, imagem, codigo_sku, estoque, categoria, proprietario_id, moeda)
SELECT 'Consultoria em Marketing Digital', 'Sessão de consultoria personalizada para melhorar sua presença online', 5000.00, 'https://via.placeholder.com/300x200?text=Marketing+Digital', 'CONS-001', 50, 'Serviços', 1, 'CVE'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM produtos LIMIT 1);

INSERT INTO produtos (nome, descricao, preco, imagem, codigo_sku, estoque, categoria, proprietario_id, moeda)
SELECT 'E-book: Finanças Pessoais', 'Guia completo para organizar suas finanças e investir com sabedoria', 2500.00, 'https://via.placeholder.com/300x200?text=Ebook+Financas', 'EBOOK-001', 999, 'E-books', 1, 'CVE'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM produtos LIMIT 1);

-- Inserir links de pagamento de exemplo (apenas se a tabela estiver vazia)
INSERT INTO links_pagamento (codigo, titulo, descricao, status, data_criacao, produto_id, proprietario_id)
SELECT 'java123456', 'Curso Java com Desconto', 'Aproveite esta oferta especial para o curso de Java', 'ATIVO', NOW(), 
       (SELECT id FROM produtos WHERE codigo_sku = 'JAVA-001' LIMIT 1), 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM links_pagamento LIMIT 1);

INSERT INTO links_pagamento (codigo, titulo, descricao, status, data_criacao, data_expiracao, quantidade_usos, usos, produto_id, proprietario_id)
SELECT 'cons123456', 'Consultoria Premium', 'Consultoria exclusiva com nossos especialistas', 'ATIVO', NOW(), 
       DATE_ADD(NOW(), INTERVAL 30 DAY), 10, 0,
       (SELECT id FROM produtos WHERE codigo_sku = 'CONS-001' LIMIT 1), 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM links_pagamento LIMIT 1);

INSERT INTO links_pagamento (codigo, titulo, descricao, status, data_criacao, produto_id, proprietario_id)
SELECT 'ebook12345', 'E-book Finanças', 'Adquira conhecimento financeiro com nosso e-book', 'ATIVO', NOW(), 
       (SELECT id FROM produtos WHERE codigo_sku = 'EBOOK-001' LIMIT 1), 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM links_pagamento LIMIT 1);
