package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tenant",
       uniqueConstraints = {
           @UniqueConstraint(name = "UK_TENANT_CODIGO", columnNames = "codigo"),
           @UniqueConstraint(name = "UK_TENANT_SLUG", columnNames = "slug")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(name = "documento", length = 50)
    private String documento;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "endereco", columnDefinition = "TEXT")
    private String endereco;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "configuracoes", columnDefinition = "TEXT")
    private String configuracoes;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "tenants", fetch = FetchType.LAZY)
    private List<UsuarioEntity> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<ConfiguracaoPagamentoEntity> configuracoesPagamento = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<ProdutoEntity> produtos = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<LinkPagamentoEntity> linksPagamento = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<PlanoEntity> planos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (ativo == null) {
            ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantEntity that = (TenantEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
