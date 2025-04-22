package cv.ntinnovation.vintipay.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
@Builder
public class UsuarioEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;
    
    private boolean ativo = true;
    
    @Column(unique = true)
    private String email;
    
    private String nome;
    
    private String telefone;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Column(name = "data_registro")
    private java.time.LocalDateTime dataRegistro;
    
    @Column(name = "ultimo_login")
    private java.time.LocalDateTime ultimoLogin;
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private PlanoAssinaturaEntity planoAssinatura;
}
