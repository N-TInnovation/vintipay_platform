CREATE TABLE IF NOT EXISTS pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intencao_pagamento_id BIGINT NOT NULL,
    fingerprint VARCHAR(255) NOT NULL,
    result_fingerprint VARCHAR(255),
    timestamp VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    mensagem TEXT,
    reference VARCHAR(255) UNIQUE,
    pan_mascarado VARCHAR(255),
    authorization_code TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_pagamento_intencao
        FOREIGN KEY (intencao_pagamento_id)
        REFERENCES intencao_pagamento (id)
);

CREATE INDEX idx_pagamento_reference ON pagamento (reference);
CREATE INDEX idx_pagamento_status ON pagamento (status);
CREATE INDEX idx_pagamento_created_at ON pagamento (created_at);
