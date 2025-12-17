package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfiguracaoPagamento {
    private Long id;
    private String posId;
    private String merchantId;
    private String apiKey;
    private String apiSecret;
    private String posAutCode;
    private String returnUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String returnAutCode;
    private String nomeLoja;
    private String urlLoja;
    private String telefone;
    private String email;
    private Usuario usuario;
    private Long usuarioId;
    private Long tenantId;
    private AmbienteEnum ambiente;
    private String webhookUrl;
    private String webhookSecret;
    private Boolean ativo;
    private Boolean webhookAtivo;
    private Boolean returnAtivo;
    private String observacoes;
    private List<ConfiguracaoIntegracoes> configuracaoAplicacoes;
}
