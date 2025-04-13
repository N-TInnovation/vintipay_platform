package cv.ntinnovation.gateway_payment.application.core.service;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.InfoContaPagamento;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class InfoContaPagamentoGeneratorService {
    
    public InfoContaPagamento generate() {
        LocalDateTime now = LocalDateTime.now();
        
        return InfoContaPagamento.builder()
                .chAccAgeInd("05") // Conta criada agora
                .chAccChange(now.toString())
                .chAccDate(now.toString())
                .chAccPwChange(now.toString())
                .chAccPwChangeInd("01") // Senha nunca alterada
                .suspiciousAccActivity("01") // Sem atividade suspeita
                .build();
    }
}
