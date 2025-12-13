package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_papel",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "papel_id")
    )
    private Set<PapelEntity> papeis = new HashSet<>();
    
    private boolean ativo = true;
    
    @Column(unique = true)
    private String email;
    
    private String nome;
    
    private String telefone;
    
    @Column(name = "nif")
    private String nif;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Column(name = "data_registro")
    private java.time.LocalDateTime dataRegistro;
    
    @Column(name = "ultimo_login")
    private java.time.LocalDateTime ultimoLogin;
    
    @Column(name = "reset_password_token")
    private String resetPasswordToken;
    
    @Column(name = "reset_password_expires")
    private java.time.LocalDateTime resetPasswordExpires;
    
    @Column(name = "email_verification_token")
    private String emailVerificationToken;
    
    @Column(name = "email_verified")
    private boolean emailVerified = false;
    
    @Column(name = "email_verification_expires")
    private java.time.LocalDateTime emailVerificationExpires;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private PlanoAssinaturaEntity planoAssinatura;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_tenant",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "tenant_id")
    )
    private Set<TenantEntity> tenants = new HashSet<>();
}
