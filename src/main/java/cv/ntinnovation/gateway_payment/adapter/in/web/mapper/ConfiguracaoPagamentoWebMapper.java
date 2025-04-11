package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ConfiguracaoPagamentoResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoPagamentoWebMapper {

    public ConfiguracaoPagamento toConfiguracao(ConfiguracaoPagamentoRequest request) {
        ConfiguracaoPagamento configuracao =  ConfiguracaoPagamento.builder().build();
        configuracao.setPosId(request.getPosId());
        configuracao.setMerchantId(request.getMerchantId());
        configuracao.setTerminalId(request.getTerminalId());
        configuracao.setApiKey(request.getApiKey());
        configuracao.setApiSecret(request.getApiSecret());
        return configuracao;
    }

    public ConfiguracaoPagamentoResponse toResponse(ConfiguracaoPagamento configuracao) {
        return ConfiguracaoPagamentoResponse.builder()
                .id(configuracao.getId())
                .posId(configuracao.getPosId())
                .merchantId(configuracao.getMerchantId())
                .terminalId(configuracao.getTerminalId())
                .apiKey(configuracao.getApiKey())
                .isActive(configuracao.getIsActive())
                .createdAt(configuracao.getCreatedAt())
                .updatedAt(configuracao.getUpdatedAt())
                .build();
    }
}
