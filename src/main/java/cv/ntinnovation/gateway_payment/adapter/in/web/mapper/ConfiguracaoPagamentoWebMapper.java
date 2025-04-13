package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
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
                .isActive(request.getIsActive())
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
                .apiKey(domain.getApiKey())
                .isActive(domain.getIsActive())
                .build();
    }
}
