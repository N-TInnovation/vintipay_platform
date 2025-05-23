package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa as preferências de notificação do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciasNotificacao {
    private Long id;
    private Long usuarioId;
    
    // Canais de notificação
    private boolean email;
    private boolean sms;
    private boolean push;
    
    // Tipos de notificação
    private boolean transacoes;
    private boolean promocoes;
    private boolean seguranca;
    
    // Valores padrão
    public static PreferenciasNotificacao getDefault(Long usuarioId) {
        return PreferenciasNotificacao.builder()
                .usuarioId(usuarioId)
                .email(true)
                .sms(true)
                .push(false)
                .transacoes(true)
                .promocoes(false)
                .seguranca(true)
                .build();
    }
}
