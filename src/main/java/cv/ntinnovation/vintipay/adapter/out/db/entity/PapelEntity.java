package cv.ntinnovation.vintipay.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "papeis")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PapelEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(name = "padrao")
    private boolean padrao;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "papel_permissao",
        joinColumns = @JoinColumn(name = "papel_id"),
        inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<PermissaoEntity> permissoes = new HashSet<>();
    
    @ManyToMany(mappedBy = "papeis")
    private Set<UsuarioEntity> usuarios = new HashSet<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
