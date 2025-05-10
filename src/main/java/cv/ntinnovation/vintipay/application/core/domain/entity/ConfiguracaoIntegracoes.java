package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class ConfiguracaoIntegracoes {
    private Long id;
    private boolean isActive;
    private IntegracaoEnum integracaoEnum;
    private String description;
    private String version;
    private String baseUrl;
    private String apiKey;
    private String apiSecret;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ConfiguracaoPagamento configuracaoPagamento;
    private Usuario usuario;

    public void setIntegracaoEnum(IntegracaoEnum integracaoEnum) {
        this.integracaoEnum = integracaoEnum;
        this.integracaoEnum.getIntegracao().setAplicacao(this);
    }
}
