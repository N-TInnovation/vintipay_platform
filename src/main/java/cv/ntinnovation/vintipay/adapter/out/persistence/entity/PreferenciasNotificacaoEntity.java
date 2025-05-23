package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade JPA para preferências de notificação do usuário
 */
@Entity
@Table(name = "preferencias_notificacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciasNotificacaoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "email", nullable = false)
    private boolean email;
    
    @Column(name = "sms", nullable = false)
    private boolean sms;
    
    @Column(name = "push", nullable = false)
    private boolean push;
    
    @Column(name = "transacoes", nullable = false)
    private boolean transacoes;
    
    @Column(name = "promocoes", nullable = false)
    private boolean promocoes;
    
    @Column(name = "seguranca", nullable = false)
    private boolean seguranca;
}
