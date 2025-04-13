ALTER TABLE configuracao_pagamento ADD COLUMN return_url VARCHAR(255) NOT NULL DEFAULT 'https://localhost:8443/admin/pagamentos';
