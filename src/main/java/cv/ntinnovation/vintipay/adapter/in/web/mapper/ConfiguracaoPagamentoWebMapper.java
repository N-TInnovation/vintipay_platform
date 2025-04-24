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
                .usuarioId(request.getUsuarioId())
                .ambiente(request.getAmbiente())
                .webhookUrl(request.getWebhookUrl())
                .webhookSecret(request.getWebhookSecret())
                .ativo(request.getAtivo())
                .webhookAtivo(request.getWebhookAtivo())
                .returnAtivo(request.getReturnAtivo())
                .observacoes(request.getObservacoes())
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
        request.setApiKey(domain.getApiKey());
        // Não incluir o apiSecret por questões de segurança
        // request.setApiSecret(domain.getApiSecret());
        request.setPosAutCode(domain.getPosAutCode());
        request.setReturnAutCode(domain.getReturnAutCode());
        request.setReturnUrl(domain.getReturnUrl());
        request.setIsActive(domain.getIsActive());
        request.setNomeLoja(domain.getNomeLoja());
        request.setUrlLoja(domain.getUrlLoja());
        request.setTelefone(domain.getTelefone());
        request.setEmail(domain.getEmail());
        request.setAmbiente(domain.getAmbiente());
        request.setWebhookUrl(domain.getWebhookUrl());
        request.setWebhookSecret(domain.getWebhookSecret());
        request.setAtivo(domain.getAtivo());
        request.setWebhookAtivo(domain.getWebhookAtivo());
        request.setReturnAtivo(domain.getReturnAtivo());
        request.setObservacoes(domain.getObservacoes());
        request.setUsuarioId(domain.getUsuarioId());
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
                .ambiente(domain.getAmbiente())
                .webhookUrl(domain.getWebhookUrl())
                .webhookSecret(domain.getWebhookSecret())
                .ativo(domain.getAtivo())
                .webhookAtivo(domain.getWebhookAtivo())
                .returnAtivo(domain.getReturnAtivo())
                .observacoes(domain.getObservacoes())
                .usuarioId(domain.getUsuarioId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
