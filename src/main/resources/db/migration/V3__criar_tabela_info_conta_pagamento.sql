-- Criação da tabela de informações da conta de pagamento
CREATE TABLE info_conta_pagamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ch_acc_age_ind VARCHAR(255),
    ch_acc_change VARCHAR(255),
    ch_acc_date VARCHAR(255),
    ch_acc_pw_change VARCHAR(255),
    ch_acc_pw_change_ind VARCHAR(255),
    suspicious_acc_activity VARCHAR(255)
);
