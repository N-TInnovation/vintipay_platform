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

    public ConfiguracaoPagamentoResponse toResponse(ConfiguracaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        return ConfiguracaoPagamentoResponse.builder()
                .id(domain.getId())
                .posId(domain.getPosId())
                .merchantId(domain.getMerchantId())
                .terminalId(domain.getTerminalId())
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
