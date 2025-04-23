package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ConfiguracaoPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoPagamentoWebMapper {

    public ConfiguracaoPagamento toDomain(ConfiguracaoPagamentoRequest request) {
        if (request == null) {
            return null;
        }

        return ConfiguracaoPagamento.builder()
                .id(request.getId())
                .posId(request.getPosId())
                .merchantId(request.getMerchantId())
                .terminalId(request.getTerminalId())
                .apiKey(request.getApiKey())
                .apiSecret(request.getApiSecret())
                .posAutCode(request.getPosAutCode())
                .returnAutCode(request.getReturnAutCode())
                .returnUrl(request.getReturnUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .nomeLoja(request.getNomeLoja())
                .urlLoja(request.getUrlLoja())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .build();
    }
    
    public ConfiguracaoPagamentoRequest toRequest(ConfiguracaoPagamento domain) {
        if (domain == null) {
            return null;
        }
        
        ConfiguracaoPagamentoRequest request = new ConfiguracaoPagamentoRequest();
        request.setId(domain.getId());
        request.setPosId(domain.getPosId());
        request.setMerchantId(domain.getMerchantId());
        request.setTerminalId(domain.getTerminalId());
        request.setApiKey(domain.getApiKey());
        request.setApiSecret(domain.getApiSecret());
        request.setPosAutCode(domain.getPosAutCode());
        request.setReturnAutCode(domain.getReturnAutCode());
        request.setReturnUrl(domain.getReturnUrl());
        request.setIsActive(domain.getIsActive());
        request.setNomeLoja(domain.getNomeLoja());
        request.setUrlLoja(domain.getUrlLoja());
        request.setTelefone(domain.getTelefone());
        request.setEmail(domain.getEmail());
        return request;
    }

    public ConfiguracaoPagamentoResponse toResponse(ConfiguracaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        return ConfiguracaoPagamentoResponse.builder()
                .id(domain.getId())
                .posId(domain.getPosId())
                .merchantId(domain.getMerchantId())
                .terminalId(domain.getTerminalId())
                .apiKey(domain.getApiKey())
                // Não incluir apiSecret na resposta por questões de segurança
                .posAutCode(domain.getPosAutCode())
                .returnAutCode(domain.getReturnAutCode())
                .returnUrl(domain.getReturnUrl())
                .isActive(domain.getIsActive())
                .nomeLoja(domain.getNomeLoja())
                .urlLoja(domain.getUrlLoja())
                .telefone(domain.getTelefone())
                .email(domain.getEmail())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
